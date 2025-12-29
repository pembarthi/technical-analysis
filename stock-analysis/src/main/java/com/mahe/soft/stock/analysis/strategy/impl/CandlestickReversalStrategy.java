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
public class CandlestickReversalStrategy implements TradingStrategy {

    private final TALibService taLibService;

    @Override
    public String getName() {
        return "Candlestick Reversal Strategy";
    }

    @Override
    public TradeSignal analyze(List<StockPriceDto> prices) {
        if (prices.size() < 10) { // arbitrary small buffer for patterns
            return TradeSignal.NONE;
        }

        double[] open = StrategyUtils.getOpenPrices(prices);
        double[] high = StrategyUtils.getHighPrices(prices);
        double[] low = StrategyUtils.getLowPrices(prices);
        double[] close = StrategyUtils.getClosePrices(prices);

        // Calculate patterns
        int[] hammers = taLibService.cdlHammer(open, high, low, close);
        int[] engulfing = taLibService.cdlEngulfing(open, high, low, close);
        int[] morningStar = taLibService.cdlMorningStar(open, high, low, close, 0.5); // 0.5 penetration default
        int[] shootingStar = taLibService.cdlShootingStar(open, high, low, close);

        int i = prices.size() - 1;

        // Bullish Patterns (> 0 or 100)
        boolean isHammer = hammers[i] > 0;
        boolean isBullishEngulfing = engulfing[i] > 0; // Positive is bullish
        boolean isMorningStar = morningStar[i] > 0;

        if (isHammer || isBullishEngulfing || isMorningStar) {
            return TradeSignal.BUY;
        }

        // Bearish Patterns (< 0 or -100)
        boolean isBearishEngulfing = engulfing[i] < 0;
        boolean isShootingStar = shootingStar[i] < 0; // Shooting star is typically bearish (-100)

        if (isBearishEngulfing || isShootingStar) {
            return TradeSignal.SELL;
        }

        return TradeSignal.HOLD;
    }
}
