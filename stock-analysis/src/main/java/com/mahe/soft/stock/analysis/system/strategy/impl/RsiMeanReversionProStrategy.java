package com.mahe.soft.stock.analysis.system.strategy.impl;

import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.BaseStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("RsiMeanReversionPro")
@RequiredArgsConstructor
public class RsiMeanReversionProStrategy extends BaseStrategy {

    private final TALibService taLibService;
    private static final int RSI_PERIOD = 14;

    @Override
    public String getName() {
        return "RSI Mean Reversion Pro";
    }

    @Override
    public TradeSignal evaluate(List<Candle> candles) {
        if (candles.size() < RSI_PERIOD + 1)
            return TradeSignal.hold();

        double[] close = getClosePrices(candles);
        double[] rsi = taLibService.rsi(close, RSI_PERIOD);

        int i = candles.size() - 1;

        if (Double.isNaN(rsi[i]))
            return TradeSignal.hold();

        if (rsi[i] < 30) {
            return TradeSignal.buy(0.8);
        }

        if (rsi[i] > 70) {
            return TradeSignal.sell(0.8);
        }

        return TradeSignal.hold();
    }
}
