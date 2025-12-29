package com.mahe.soft.stock.analysis.strategy.impl;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.strategy.StrategyUtils;
import com.mahe.soft.stock.analysis.strategy.TradeSignal;
import com.mahe.soft.stock.analysis.strategy.TradingStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultiIndicatorStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int EMA_FILTER = 200;
    private static final int RSI_PERIOD = 14;

    @Override
    public String getName() {
        return "Multi-Indicator Confirmation Strategy";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < EMA_FILTER + 1) {
            return TradeSignal.NONE;
        }

        double[] open = StrategyUtils.getOpenPrices(prices);
        double[] high = StrategyUtils.getHighPrices(prices);
        double[] low = StrategyUtils.getLowPrices(prices);
        double[] close = StrategyUtils.getClosePrices(prices);

        // Indicators
        double[] ema200 = taLibService.ema(close, EMA_FILTER);
        double[] rsi = taLibService.rsi(close, RSI_PERIOD);

        // Patterns (using just generic 'bullish' check on few robust ones)
        int[] engulfing = taLibService.cdlEngulfing(open, high, low, close);
        int[] morningStar = taLibService.cdlMorningStar(open, high, low, close, 0.5);
        int[] hammer = taLibService.cdlHammer(open, high, low, close);

        int i = prices.size() - 1;

        if (Double.isNaN(ema200[i]) || Double.isNaN(rsi[i]))
            return TradeSignal.NONE;

        double currentPrice = close[i];

        // ENTRY RULE: Buy
        // 1. Price > 200 EMA
        boolean trendUp = currentPrice > ema200[i];

        // 2. RSI between 40-60 (Momentum building but not overbought)
        boolean rsiCondition = rsi[i] >= 40 && rsi[i] <= 60;

        // 3. Bullish Pattern
        boolean patternFound = (engulfing[i] > 0) || (morningStar[i] > 0) || (hammer[i] > 0);

        if (trendUp && rsiCondition && patternFound) {
            return TradeSignal.BUY;
        }

        // EXIT RULE: Sell
        // 1. RSI > 70
        boolean overbought = rsi[i] > 70;

        // 2. Bearish Pattern?
        boolean bearishPattern = (engulfing[i] < 0); // or shooting star etc.

        if (overbought || bearishPattern) {
            return TradeSignal.SELL;
        }

        return TradeSignal.HOLD;
    }
}
