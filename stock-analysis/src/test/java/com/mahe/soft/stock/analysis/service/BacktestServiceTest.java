package com.mahe.soft.stock.analysis.service;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.strategy.TradingStrategy;
import com.mahe.soft.stock.analysis.strategy.impl.RsiStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BacktestServiceTest {

    @Mock
    private StockDbClient stockDbClient;

    @Spy
    private List<TradingStrategy> strategies = new ArrayList<>();

    @InjectMocks
    private BacktestService backtestService;

    @Test
    void testRunBacktest_StrategyNotFound() {
        assertThrows(RuntimeException.class, () -> 
            backtestService.runBacktest("AAPL", "UnknownStrategy"));
    }

    @Test
    void testRunBacktest_Success() {
        RsiStrategy rsi = org.mockito.Mockito.mock(RsiStrategy.class);
        when(rsi.getName()).thenReturn("RSI Strategy");
        strategies.add(rsi);

        when(stockDbClient.getStockPrices("AAPL")).thenReturn(Collections.emptyList());
        when(rsi.analyze(any())).thenReturn(com.mahe.soft.stock.analysis.strategy.TradeSignal.HOLD);

        String result = backtestService.runBacktest("AAPL", "RSI Strategy");
        assertTrue(result.contains("HOLD"));
    }
}
