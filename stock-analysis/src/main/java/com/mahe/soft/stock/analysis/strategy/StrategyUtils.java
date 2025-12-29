package com.mahe.soft.stock.analysis.strategy;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import java.util.List;

public class StrategyUtils {

    public static double[] getClosePrices(List<StockPriceDto> prices) {
        return prices.stream()
                .mapToDouble(p -> p.getClosePrice().doubleValue())
                .toArray();
    }

    public static double[] getHighPrices(List<StockPriceDto> prices) {
        return prices.stream()
                .mapToDouble(p -> p.getHighPrice().doubleValue())
                .toArray();
    }

    public static double[] getLowPrices(List<StockPriceDto> prices) {
        return prices.stream()
                .mapToDouble(p -> p.getLowPrice().doubleValue())
                .toArray();
    }

    public static double[] getOpenPrices(List<StockPriceDto> prices) {
        return prices.stream()
                .mapToDouble(p -> p.getOpenPrice().doubleValue())
                .toArray();
    }

    public static double[] getVolumes(List<StockPriceDto> prices) {
        return prices.stream()
                .mapToDouble(p -> p.getVolume().doubleValue())
                .toArray();
    }
}
