package com.mahe.soft.stock.analysis.system.strategy.impl;

import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.BaseStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("EmaCrossoverPro")
@RequiredArgsConstructor
@Slf4j
public class EmaCrossoverProStrategy extends BaseStrategy {

    private final TALibService taLibService;
    private static final int FAST = 21;
    private static final int SLOW = 55;

    @Override
    public String getName() {
        return "EMA Crossover Pro (21/55)";
    }

    @Override
    public TradeSignal evaluate(List<Candle> candles) {
        if (candles.size() < SLOW + 1)
            return TradeSignal.hold();

        double[] close = getClosePrices(candles);
        double[] emaFast = taLibService.ema(close, FAST);
        double[] emaSlow = taLibService.ema(close, SLOW);

        int i = candles.size() - 1;
        int prev = i - 1;

        if (Double.isNaN(emaFast[prev]) || Double.isNaN(emaSlow[prev]))
            return TradeSignal.hold();

        boolean prevAbove = emaFast[prev] > emaSlow[prev];
        boolean currAbove = emaFast[i] > emaSlow[i];

        if (!prevAbove && currAbove) {
            log.debug("Genering BUY signal for EMA Crossover");
            return TradeSignal.buy(1.0);
        }

        if (prevAbove && !currAbove) {
            log.debug("Genering SELL signal for EMA Crossover");
            return TradeSignal.sell(1.0);
        }

        return TradeSignal.hold();
    }
}
