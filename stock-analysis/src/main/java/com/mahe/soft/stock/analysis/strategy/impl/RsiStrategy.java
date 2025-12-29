package com.mahe.soft.stock.analysis.strategy.impl;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.strategy.TradeSignal;
import com.mahe.soft.stock.analysis.strategy.TradingStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RsiStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int PERIOD = 14;

    @Override
    public String getName() {
        return "RSI Strategy";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < PERIOD + 1) {
            return TradeSignal.NONE;
        }

        double[] closePrices = prices.stream()
                .mapToDouble(p -> p.getClosePrice().doubleValue())
                .toArray();

        // TA-Lib requires doubles
        double[] rsiValues = taLibService.rsi(closePrices, PERIOD);

        // Since we align the result in TALibService, the RSI array matches the input
        // price array length.
        // The last element of rsiValues corresponds to the last element of closePrices.
        // If there isn't enough data, it will be NaN.
        double lastRsi = rsiValues[prices.size() - 1];

        if (Double.isNaN(lastRsi)) {
            return TradeSignal.NONE;
        }

        if (lastRsi < 30)
            return TradeSignal.BUY;
        if (lastRsi > 70)
            return TradeSignal.SELL;

        return TradeSignal.HOLD;
    }
}
