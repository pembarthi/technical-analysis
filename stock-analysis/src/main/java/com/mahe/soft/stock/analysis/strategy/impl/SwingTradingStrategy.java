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
public class SwingTradingStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int EMA_PERIOD = 21;
    private static final int RSI_PERIOD = 14;

    @Override
    public String getName() {
        return "Swing Trading Strategy (7-20 Days)";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < EMA_PERIOD + 1) {
            return TradeSignal.NONE;
        }

        double[] open = StrategyUtils.getOpenPrices(prices);
        double[] high = StrategyUtils.getHighPrices(prices);
        double[] low = StrategyUtils.getLowPrices(prices);
        double[] close = StrategyUtils.getClosePrices(prices);

        double[] ema21 = taLibService.ema(close, EMA_PERIOD);
        double[] rsi = taLibService.rsi(close, RSI_PERIOD);

        // Bullish candle check (generic)
        int[] hammer = taLibService.cdlHammer(open, high, low, close);
        int[] engulfing = taLibService.cdlEngulfing(open, high, low, close);

        int i = prices.size() - 1;
        if (Double.isNaN(ema21[i]) || Double.isNaN(rsi[i]))
            return TradeSignal.NONE;

        double currentPrice = close[i];
        double lowPrice = low[i];

        // Entry:
        // 1. Pullback to EMA(21): Low touches EMA or Close is near EMA?
        // Let's say Low <= EMA21 && Close > EMA21 (Support validation)
        boolean nearEma = (lowPrice <= ema21[i] * 1.01) && (currentPrice >= ema21[i]);

        // 2. RSI > 40
        boolean rsiCondition = rsi[i] > 40;

        // 3. Bullish candle
        boolean bullishCandle = (hammer[i] > 0) || (engulfing[i] > 0);

        if (nearEma && rsiCondition && bullishCandle) {
            return TradeSignal.BUY;
        }

        return TradeSignal.HOLD;
    }
}
