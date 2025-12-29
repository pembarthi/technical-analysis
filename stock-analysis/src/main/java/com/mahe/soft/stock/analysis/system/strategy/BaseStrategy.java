package com.mahe.soft.stock.analysis.system.strategy;

import com.mahe.soft.stock.analysis.system.model.Candle;
import java.util.List;

public abstract class BaseStrategy implements TradingStrategy {

    protected double[] getClosePrices(List<Candle> candles) {
        return candles.stream().mapToDouble(Candle::getClose).toArray();
    }

    protected double[] getHighPrices(List<Candle> candles) {
        return candles.stream().mapToDouble(Candle::getHigh).toArray();
    }

    protected double[] getLowPrices(List<Candle> candles) {
        return candles.stream().mapToDouble(Candle::getLow).toArray();
    }

    protected double[] getOpenPrices(List<Candle> candles) {
        return candles.stream().mapToDouble(Candle::getOpen).toArray();
    }
}
