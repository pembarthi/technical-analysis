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
public class MacdStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int FAST_PERIOD = 12;
    private static final int SLOW_PERIOD = 26;
    private static final int SIGNAL_PERIOD = 9;

    @Override
    public String getName() {
        return "MACD Trend Continuation Strategy";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        // Need enough data for Slow Period + Signal Period roughly
        if (prices.size() < SLOW_PERIOD + SIGNAL_PERIOD) {
            return TradeSignal.NONE;
        }

        double[] closePrices = StrategyUtils.getClosePrices(prices);

        // MACD returns [MACD, Signal, Hist]
        double[][] macdResult = taLibService.macd(closePrices, FAST_PERIOD, SLOW_PERIOD, SIGNAL_PERIOD);
        double[] macdLine = macdResult[0];
        double[] signalLine = macdResult[1];
        double[] hist = macdResult[2];

        int lastIdx = prices.size() - 1;
        int prevIdx = lastIdx - 1;

        if (Double.isNaN(macdLine[prevIdx]) || Double.isNaN(signalLine[prevIdx])) {
            return TradeSignal.NONE;
        }

        // Buy Condition: MACD crosses above Signal line AND Histogram is positive
        // (implied by crossover usually, but explicit check requested)
        // "Confirm with histogram turning positive" -> usually means Hist > 0

        boolean prevMacdBelowSignal = macdLine[prevIdx] < signalLine[prevIdx];
        boolean currMacdAboveSignal = macdLine[lastIdx] > signalLine[lastIdx];
        boolean histPositive = hist[lastIdx] > 0;

        if (prevMacdBelowSignal && currMacdAboveSignal && histPositive) {
            return TradeSignal.BUY;
        }

        // Sell Condition: MACD crosses below Signal line?
        // User didn't strictly specify sell, but symmetrical logic implies it.
        boolean prevMacdAboveSignal = macdLine[prevIdx] > signalLine[prevIdx];
        boolean currMacdBelowSignal = macdLine[lastIdx] < signalLine[lastIdx];

        if (prevMacdAboveSignal && currMacdBelowSignal) {
            return TradeSignal.SELL;
        }

        return TradeSignal.HOLD;
    }
}
