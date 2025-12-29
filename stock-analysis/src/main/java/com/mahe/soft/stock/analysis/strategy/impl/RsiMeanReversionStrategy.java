package com.mahe.soft.stock.analysis.strategy.impl;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.strategy.StrategyUtils;
import com.mahe.soft.stock.analysis.strategy.TradeSignal;
import com.mahe.soft.stock.analysis.strategy.TradingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RsiMeanReversionStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int RSI_PERIOD = 14;
    private static final int EMA_FILTER_PERIOD = 200;

    @Override
    public String getName() {
        return "RSI Mean Reversion Strategy";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < EMA_FILTER_PERIOD + 1) {
            return TradeSignal.NONE;
        }

        double[] closePrices = StrategyUtils.getClosePrices(prices);
        double[] rsi = taLibService.rsi(closePrices, RSI_PERIOD);
        double[] ema200 = taLibService.ema(closePrices, EMA_FILTER_PERIOD);

        int i = prices.size() - 1;

        if (Double.isNaN(rsi[i]) || Double.isNaN(ema200[i])) {
            return TradeSignal.NONE;
        }

        double currentPrice = closePrices[i];
        double currentRsi = rsi[i];
        double longTermTrend = ema200[i];

        // Buy: RSI < 30 (Oversold) AND Price > 200 EMA (Uptrend filter)
        if (currentRsi < 30 && currentPrice > longTermTrend) {
            return TradeSignal.BUY;
        }

        // Sell: RSI > 70 (Overbought) - Filter? Usually mean reversion sells on
        // overbought.
        if (currentRsi > 70) {
            return TradeSignal.SELL;
        }

        return TradeSignal.HOLD;
    }
}
