package com.mahe.soft.stock.analysis.system.strategy;

import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import java.util.List;

public interface TradingStrategy {
    String getName();

    /**
     * Evaluates the market data and generates a signal for the *current* (last)
     * candle.
     * 
     * @param candles Historical data up to the current moment. Last element is the
     *                latest.
     * @return TradeSignal (BUY, SELL, HOLD) with confidence.
     */
    TradeSignal evaluate(List<Candle> candles);
}
