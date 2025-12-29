package com.mahe.soft.stock.analysis.strategy.impl;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.strategy.StrategyUtils;
import com.mahe.soft.stock.analysis.strategy.TradeSignal;
import com.mahe.soft.stock.analysis.strategy.TradingStrategy;
import com.tictactec.ta.lib.Core;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BreakoutVolumeStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private final Core core = new Core(); // Need MAX which isn't in service yet or I can use native Java max

    private static final int LOOKBACK_PERIOD = 20; // "Recent high"
    private static final int VOL_SMA_PERIOD = 20;

    @Override
    public String getName() {
        return "Breakout + Volume Confirmation";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < LOOKBACK_PERIOD + 1) {
            return TradeSignal.NONE;
        }

        double[] high = StrategyUtils.getHighPrices(prices);
        double[] close = StrategyUtils.getClosePrices(prices);
        double[] volume = StrategyUtils.getVolumes(prices);

        int i = prices.size() - 1;

        // 1. Calculate Recent High (excluding current candle to check if we broke it)
        // Using Core.max directly or naive loop.
        // Let's use naive loop over window [i - LOOKBACK, i - 1]
        double recentHigh = 0;
        for (int k = 1; k <= LOOKBACK_PERIOD; k++) {
            if (high[i - k] > recentHigh) {
                recentHigh = high[i - k];
            }
        }

        // 2. Identify Breakout
        boolean priceBreakout = close[i] > recentHigh;

        // 3. Volume Confirmation
        double[] volSma = taLibService.sma(volume, VOL_SMA_PERIOD);
        double avgVol = volSma[i]; // aligned

        if (Double.isNaN(avgVol))
            return TradeSignal.NONE;

        boolean volConfirmation = volume[i] > (1.5 * avgVol);

        if (priceBreakout && volConfirmation) {
            return TradeSignal.BUY;
        }

        return TradeSignal.HOLD;
    }
}
