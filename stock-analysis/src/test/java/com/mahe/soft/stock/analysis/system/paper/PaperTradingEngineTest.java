package com.mahe.soft.stock.analysis.system.paper;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.system.mapper.CandleMapper;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaperTradingEngineTest {

    @Mock
    private StockDbClient stockDbClient;
    @Mock
    private CandleMapper candleMapper;
    @Mock
    private TradingStrategy strategy;

    private PaperTradingEngine engine;

    @BeforeEach
    void setUp() {
        Map<String, TradingStrategy> strategies = Collections.singletonMap("TestStrategy", strategy);
        engine = new PaperTradingEngine(stockDbClient, candleMapper, strategies);
    }

    @Test
    void testSimulationFlow_Buy() {
        // 1. Start Session
        String sessionId = engine.startSimulation("AAPL", "TestStrategy", 1000.0);
        assertNotNull(sessionId);

        // 2. Mock Data
        StockPriceDto priceDto = new StockPriceDto();
        priceDto.setClosePrice(new java.math.BigDecimal("100.0"));

        List<StockPriceDto> prices = Collections.singletonList(priceDto);
        when(stockDbClient.getStockPrices("AAPL")).thenReturn(prices);

        Candle candle = Candle.builder().close(100.0).build();
        when(candleMapper.toCandles(prices)).thenReturn(Collections.singletonList(candle));

        // 3. Mock Strategy Signal (BUY)
        when(strategy.evaluate(any())).thenReturn(TradeSignal.buy(1.0));

        // 4. Run Tick
        engine.runSimulationTick();

        // 5. Verify Account State
        PaperAccount account = engine.getAccountStatus(sessionId);

        // Should have bought 10 shares (1000 / 100)
        assertEquals(1, account.getPositions().size());
        assertEquals(10, account.getPositions().get(0).getQuantity());
        assertEquals(100.0, account.getPositions().get(0).getEntryPrice());
        assertEquals(0.0, account.getCashBalance()); // All spent
    }
}
