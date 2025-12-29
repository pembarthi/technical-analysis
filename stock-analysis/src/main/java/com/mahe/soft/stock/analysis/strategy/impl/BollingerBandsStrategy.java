package com.mahe.soft.stock.analysis.strategy.impl;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.strategy.StrategyUtils;
import com.mahe.soft.stock.analysis.strategy.TradeSignal;
import com.mahe.soft.stock.analysis.strategy.TradingStrategy;
import com.tictactec.ta.lib.MAType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BollingerBandsStrategy implements TradingStrategy {

    private final TALibService taLibService;
    private static final int TIME_PERIOD = 20;
    private static final double NB_DEV_UP = 2.0;
    private static final double NB_DEV_DN = 2.0;

    // Threshold for Band Width to define a "Squeeze" (e.g., < 5% width) - this is
    // arbitrary without user input, setting a reasonable default or dynamic?
    // User said: "Detect low volatility squeeze. Band width below threshold"
    // Let's use a relative width threshold: (Upper - Lower) / Middle
    private static final double SQUEEZE_THRESHOLD = 0.05;

    @Override
    public String getName() {
        return "Bollinger Bands Squeeze Breakout";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < TIME_PERIOD + 1) {
            return TradeSignal.NONE;
        }

        double[] closePrices = StrategyUtils.getClosePrices(prices);

        // BBANDS returns [Upper, Middle, Lower]
        double[][] bbands = taLibService.bbands(closePrices, TIME_PERIOD, NB_DEV_UP, NB_DEV_DN, MAType.Sma);
        double[] upper = bbands[0];
        double[] middle = bbands[1];
        double[] lower = bbands[2];

        int i = prices.size() - 1;
        int prev = i - 1;

        if (Double.isNaN(upper[prev]))
            return TradeSignal.NONE;

        double currentPrice = closePrices[i];
        double prevPrice = closePrices[prev];

        // Squeeze Detection (on previous candle usually, to set up the breakout)
        double prevBandWidth = (upper[prev] - lower[prev]) / middle[prev];
        boolean wasSqueeze = prevBandWidth < SQUEEZE_THRESHOLD;

        // Breakout Logic
        // Buy: Price breaks above Upper Band
        // Sell: Price breaks below Lower Band

        // Note: Strict "Squeeze Breakout" means we were in a squeeze and NOW we broke
        // out.
        // Let's check if previous width was low.

        // Relaxing squeeze check slightly for demo or using just breakout if squeeze is
        // too rare.
        // User requested "Detect low volatility squeeze", implying it's a prerequisite.

        if (wasSqueeze) {
            if (currentPrice > upper[i] && prevPrice <= upper[prev]) {
                return TradeSignal.BUY;
            }
            if (currentPrice < lower[i] && prevPrice >= lower[prev]) {
                return TradeSignal.SELL;
            }
        } else {
            // Optional: Plain breakout without strict previous squeeze?
            // Implementing strict as requested.
        }

        return TradeSignal.HOLD;
    }
}
