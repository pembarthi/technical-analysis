package com.mahe.soft.stock.analysis.system.backtest;

import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BacktestEngineTest {

    @InjectMocks
    private BacktestEngine backtestEngine;

    @Mock
    private TradingStrategy strategy;

    @Test
    void testRunBacktest_Win() {
        // Mock Strategy Name
        when(strategy.getName()).thenReturn("TestStrategy");

        // Create simplistic candle history
        List<Candle> history = new ArrayList<>();
        // 100 candles
        for (int i = 0; i < 100; i++) {
            history.add(Candle.builder()
                    .timestamp(LocalDate.now().minusDays(100 - i))
                    .close(100 + i) // Price goes up steadily
                    .build());
        }

        // Strategy buys at index 50, sells at index 60
        when(strategy.evaluate(any())).thenAnswer(invocation -> {
            List<Candle> context = invocation.getArgument(0);
            int size = context.size();
            // indices are size-1.
            // We want context corresponding to original list index 50 -> size 51
            if (size == 51)
                return TradeSignal.buy(1.0);
            if (size == 61)
                return TradeSignal.sell(1.0);
            return TradeSignal.hold();
        });

        BacktestResult result = backtestEngine.runBacktest(strategy, history, 10000);

        assertNotNull(result);
        assertEquals(1, result.getTotalTrades());
        assertEquals(1, result.getWinningTrades());
        assertEquals(0, result.getLosingTrades());
        assertEquals(1.0, result.getWinRate());

        // Buy @ 150 (index 50 close), Sell @ 160 (index 60 close)
        // Profit approx 10 per share.
        assertTrue(result.getTotalReturnPercent() > 0);
    }
}
