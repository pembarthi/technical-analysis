package com.mahe.soft.stock.analysis.strategy;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import java.util.List;

public interface TradingStrategy {
    String getName();

    // Returns a list of signals, same size as input or mapped by date
    // For simplicity, let's return a detailed report or just the last signal
    TradeSignal analyze(List<StockPriceDto> prices);
}
