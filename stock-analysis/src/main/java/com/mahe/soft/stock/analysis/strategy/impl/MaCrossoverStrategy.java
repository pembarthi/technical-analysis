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
public class MaCrossoverStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int FAST_PERIOD = 12;
    private static final int SLOW_PERIOD = 26;

    @Override
    public String getName() {
        return "MA Crossover Strategy";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < SLOW_PERIOD + 1) {
            return TradeSignal.NONE;
        }

        double[] closePrices = StrategyUtils.getClosePrices(prices);
        double[] fastEma = taLibService.ema(closePrices, FAST_PERIOD);
        double[] slowEma = taLibService.ema(closePrices, SLOW_PERIOD);

        int lastIdx = prices.size() - 1;
        int prevIdx = lastIdx - 1;

        if (Double.isNaN(fastEma[prevIdx]) || Double.isNaN(slowEma[prevIdx])) {
            return TradeSignal.NONE;
        }

        boolean prevAbove = fastEma[prevIdx] > slowEma[prevIdx];
        boolean currAbove = fastEma[lastIdx] > slowEma[lastIdx];

        // Crossover UP: Prev was below (or equal), Curr is above
        if (!prevAbove && currAbove) {
            return TradeSignal.BUY;
        }

        // Crossover DOWN: Prev was above, Curr is below (or equal)
        if (prevAbove && !currAbove) {
            return TradeSignal.SELL;
        }

        return TradeSignal.HOLD;
    }
}
