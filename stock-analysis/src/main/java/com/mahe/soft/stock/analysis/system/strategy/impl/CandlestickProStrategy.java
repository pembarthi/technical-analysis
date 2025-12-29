package com.mahe.soft.stock.analysis.system.strategy.impl;

import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.BaseStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("CandlestickPro")
@RequiredArgsConstructor
public class CandlestickProStrategy extends BaseStrategy {

    private final TALibService taLibService;

    @Override
    public String getName() {
        return "Candlestick Pattern Pro";
    }

    @Override
    public TradeSignal evaluate(List<Candle> candles) {
        if (candles.size() < 5)
            return TradeSignal.hold();

        double[] open = getOpenPrices(candles);
        double[] high = getHighPrices(candles);
        double[] low = getLowPrices(candles);
        double[] close = getClosePrices(candles);

        int[] engulfing = taLibService.cdlEngulfing(open, high, low, close);
        int[] morningStar = taLibService.cdlMorningStar(open, high, low, close, 0.5);
        int[] hammer = taLibService.cdlHammer(open, high, low, close);

        int i = candles.size() - 1;

        if (engulfing[i] > 0 || morningStar[i] > 0 || hammer[i] > 0) {
            return TradeSignal.buy(0.7);
        }

        if (engulfing[i] < 0) {
            return TradeSignal.sell(0.7);
        }

        return TradeSignal.hold();
    }
}
