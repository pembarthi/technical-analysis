package com.mahe.soft.stock.analysis.system.strategy.impl;

import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.BaseStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("MacdTrendPro")
@RequiredArgsConstructor
public class MacdTrendProStrategy extends BaseStrategy {

    private final TALibService taLibService;
    private static final int FAST = 12;
    private static final int SLOW = 26;
    private static final int SIGNAL = 9;

    @Override
    public String getName() {
        return "MACD Trend Pro";
    }

    @Override
    public TradeSignal evaluate(List<Candle> candles) {
        if (candles.size() < SLOW + SIGNAL)
            return TradeSignal.hold();

        double[] close = getClosePrices(candles);
        double[][] macd = taLibService.macd(close, FAST, SLOW, SIGNAL);
        double[] hist = macd[2];

        int i = candles.size() - 1;
        int prev = i - 1;

        if (Double.isNaN(hist[prev]))
            return TradeSignal.hold();

        // Macd Crossover via Histogram (Hist crosses 0)
        boolean prevNeg = hist[prev] <= 0;
        boolean currPos = hist[i] > 0;

        if (prevNeg && currPos) {
            return TradeSignal.buy(0.9);
        }

        boolean prevPos = hist[prev] >= 0;
        boolean currNeg = hist[i] < 0;

        if (prevPos && currNeg) {
            return TradeSignal.sell(0.9);
        }

        return TradeSignal.hold();
    }
}
