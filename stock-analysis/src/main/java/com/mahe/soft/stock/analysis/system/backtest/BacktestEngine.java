package com.mahe.soft.stock.analysis.system.backtest;

import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.Trade;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BacktestEngine {

    /**
     * Runs a backtest for a given strategy and historical candles.
     * Assumes fixed position sizing (e.g., investing available capital or fixed
     * amount).
     */
    public BacktestResult runBacktest(TradingStrategy strategy, List<Candle> history, double initialCapital) {
        log.info("Starting backtest for strategy: {} on {} candles", strategy.getName(), history.size());

        List<Trade> closedTrades = new ArrayList<>();
        Trade currentTrade = null;
        double cash = initialCapital;

        // Equity curve for Drawdown / Sharpe
        List<Double> equityCurve = new ArrayList<>();
        equityCurve.add(initialCapital);

        // ATR for stops (optional, not implemented in generic engine yet, relying on
        // strategy SELL)

        // Iterate through history
        // We need a minimum window for the strategy to work.
        // We will simulate "stepping through time" by passing sub-lists or
        // passing the full list but only allowing the strategy to see up to index 'i'.
        // PASSING SUB-LISTS IS EXPENSIVE (O(N^2)).
        // Optimization: The 'TradingStrategy.evaluate' usually just needs the whole
        // list
        // and treats the last one as 'current'.
        // But for backtesting, we need to generate signal for EACH past candle.
        // TA-Lib functions calculate for the whole array at once.
        // calling evaluate(sublist) N times is slow but correct for "Simulation" style.
        // For performance, strategies should ideally support "batch evaluation", but
        // per prompt
        // "evaluate(List<Candle>)" implies single point evaluation.

        // We will start from a reasonable index (e.g. 50) to allow indicators to warm
        // up.
        int warmupPeriod = 50;

        for (int i = warmupPeriod; i < history.size(); i++) {
            // Snapshot of history up to i
            List<Candle> currentContext = history.subList(0, i + 1);
            Candle currentCandle = history.get(i);

            // Get Signal
            TradeSignal signal = strategy.evaluate(currentContext);

            // Execute Logic (Long Only)
            if (currentTrade == null) {
                if (signal.getType() == TradeSignal.Type.BUY) {
                    // Enter Trade
                    // Fixed position: Spend all cash? Or fixed amount?
                    // Let's implement "Compounding" - invest all current cash.
                    double entryPrice = currentCandle.getClose(); // Simplified: Buy at Close
                    int quantity = (int) (cash / entryPrice);

                    if (quantity > 0) {
                        double cost = quantity * entryPrice;
                        cash -= cost;

                        currentTrade = Trade.builder()
                                .symbol("TEST") // Symbol not in Candle, usually passed in context
                                .strategyName(strategy.getName())
                                .type(Trade.Type.LONG)
                                .entryDate(currentCandle.getTimestamp())
                                .entryPrice(entryPrice)
                                .quantity(quantity)
                                .build();

                        log.debug("BUY at {} price {}", currentCandle.getTimestamp(), entryPrice);
                    }
                }
            } else {
                // We have an open position
                if (signal.getType() == TradeSignal.Type.SELL) {
                    // Exit Trade
                    double exitPrice = currentCandle.getClose();
                    double revenue = currentTrade.getQuantity() * exitPrice;
                    cash += revenue;

                    currentTrade.setExitDate(currentCandle.getTimestamp());
                    currentTrade.setExitPrice(exitPrice);
                    currentTrade.setPnl(revenue - (currentTrade.getQuantity() * currentTrade.getEntryPrice()));
                    currentTrade.setPnlPercent(
                            currentTrade.getPnl() / (currentTrade.getQuantity() * currentTrade.getEntryPrice()));

                    closedTrades.add(currentTrade);
                    currentTrade = null;

                    log.debug("SELL at {} price {}", currentCandle.getTimestamp(), exitPrice);
                }
            }

            // Update Equity Curve
            double currentEquity = cash;
            if (currentTrade != null) {
                currentEquity += currentTrade.getQuantity() * currentCandle.getClose();
            }
            equityCurve.add(currentEquity);
        }

        double finalEquity = equityCurve.get(equityCurve.size() - 1);

        return calculateMetrics(closedTrades, equityCurve, initialCapital, finalEquity, strategy.getName());
    }

    private BacktestResult calculateMetrics(List<Trade> trades, List<Double> equityCurve, double initialCapital,
            double finalCapital, String strategyName) {
        int totalTrades = trades.size();
        int winningTrades = 0;
        int losingTrades = 0;
        double totalGrossProfit = 0;
        double totalGrossLoss = 0;

        for (Trade t : trades) {
            if (t.getPnl() > 0) {
                winningTrades++;
                totalGrossProfit += t.getPnl();
            } else {
                losingTrades++;
                totalGrossLoss += Math.abs(t.getPnl());
            }
        }

        double winRate = totalTrades > 0 ? (double) winningTrades / totalTrades : 0.0;
        double profitFactor = totalGrossLoss > 0 ? totalGrossProfit / totalGrossLoss
                : (totalGrossProfit > 0 ? Double.POSITIVE_INFINITY : 0.0);
        double totalReturnPercent = ((finalCapital - initialCapital) / initialCapital) * 100;

        // Max Drawdown
        double maxDrawdown = 0.0;
        double peak = -1.0;
        for (double val : equityCurve) {
            if (val > peak)
                peak = val;
            double drawdown = (peak - val) / peak;
            if (drawdown > maxDrawdown)
                maxDrawdown = drawdown;
        }

        // Sharpe Ratio (Simplified: using daily returns assuming steps are days)
        double sharpe = calculateSharpeRatio(equityCurve);

        return BacktestResult.builder()
                .strategyName(strategyName)
                .symbol("N/A") // passed separate usually
                .totalReturnPercent(totalReturnPercent)
                .winRate(winRate)
                .maxDrawdownPercent(maxDrawdown * 100)
                .sharpeRatio(sharpe)
                .profitFactor(profitFactor)
                .totalTrades(totalTrades)
                .winningTrades(winningTrades)
                .losingTrades(losingTrades)
                .trades(trades)
                .initialCapital(initialCapital)
                .finalCapital(finalCapital)
                .build();
    }

    private double calculateSharpeRatio(List<Double> equityCurve) {
        if (equityCurve.size() < 2)
            return 0.0;

        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < equityCurve.size(); i++) {
            double prev = equityCurve.get(i - 1);
            double curr = equityCurve.get(i);
            returns.add((curr - prev) / prev);
        }

        double mean = returns.stream().mapToDouble(d -> d).average().orElse(0.0);
        double variance = returns.stream().mapToDouble(d -> Math.pow(d - mean, 2)).average().orElse(0.0);
        double stdDev = Math.sqrt(variance);

        // annualized sharpe (assuming daily data, 252 trading days) - simplified
        // Sharpe = (Mean Return - Risk Free) / StdDev. Risk Free = 0 for simplicity.
        if (stdDev == 0)
            return 0.0;
        return (mean / stdDev) * Math.sqrt(252);
    }
}
