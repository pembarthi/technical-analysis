package com.mahe.soft.stock.analysis.service;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.strategy.TradeSignal;
import com.mahe.soft.stock.analysis.strategy.TradingStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BacktestService {

    private final StockDbClient stockDbClient;
    private final List<TradingStrategy> strategies;

    public String runBacktest(String symbol, String strategyName) {
        List<StockPriceDto> prices = stockDbClient.getStockPrices(symbol);

        TradingStrategy strategy = strategies.stream()
                .filter(s -> s.getName().equalsIgnoreCase(strategyName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Strategy not found: " + strategyName));

        // In a real backtest, we would walk through time.
        // For this MVP, we just ask the strategy for a signal on the whole dataset.
        // A real backtest would require the strategy to handle "up to date X" data or
        // the engine simulates the feed frame by frame.

        // This simple implementation serves as a placeholder.
        TradeSignal signal = strategy.analyze(prices);

        return "Backtest for " + symbol + " using " + strategyName + ": Resulting Signal " + signal;
    }
}
