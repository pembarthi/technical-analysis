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
public class AtrRiskStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int ATR_PERIOD = 14;

    @Override
    public String getName() {
        return "ATR-Based Risk Management Strategy"; // Note: This is usually a helper, not a signal generator, but
                                                     // implementing as requested.
    }

    /**
     * This strategy focuses on providing data for Stops,
     * but to technically "generate signals" it needs an entry condition.
     * The prompt implies "Use ATR for dynamic stops" but doesn't strictly say WHEN
     * to enter.
     * I will implement a basic Trend Following entry (SMA crossover 20/50) just to
     * demonstrate uses of ATR stops in "analyze".
     * OR, I can return NONE and assume this class is for reference?
     * No, user asked for "Java trading Strategies".
     * I'll implement a simple breakout Entry with ATR trailing stop logic
     * simulation?
     *
     * Let's make it a simple Volatility Breakout strategy:
     * Buy if Price > Price[i-1] + ATR
     * Sell if Price < Price[i-1] - ATR
     */
    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < ATR_PERIOD + 1) {
            return TradeSignal.NONE;
        }

        double[] high = StrategyUtils.getHighPrices(prices);
        double[] low = StrategyUtils.getLowPrices(prices);
        double[] close = StrategyUtils.getClosePrices(prices);

        double[] atr = taLibService.atr(high, low, close, ATR_PERIOD);

        int i = prices.size() - 1;
        if (Double.isNaN(atr[i]))
            return TradeSignal.NONE;

        double currentAtr = atr[i];
        double currentPrice = close[i];
        double prevPrice = close[i - 1];

        // Volatility Breakout Logic
        if (currentPrice > prevPrice + currentAtr) {
            return TradeSignal.BUY;
        }

        if (currentPrice < prevPrice - currentAtr) {
            return TradeSignal.SELL;
        }

        return TradeSignal.HOLD;
    }
}
