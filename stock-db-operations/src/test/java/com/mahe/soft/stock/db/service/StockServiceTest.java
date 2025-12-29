package com.mahe.soft.stock.db.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mahe.soft.stock.db.entity.StockPrice;
import com.mahe.soft.stock.db.repository.StockPriceRepository;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockPriceRepository repository;

    @InjectMocks
    private StockService stockService;

    @Test
    void testGetStockPrices() {
        String symbol = "AAPL";
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);

        when(repository.findBySymbolAndTradeDateBetweenOrderByTradeDateAsc(symbol, start, end))
                .thenReturn(Collections.emptyList());

        List<StockPrice> result = stockService.getStockPrices(symbol, start, end);

        assertNotNull(result);
        verify(repository).findBySymbolAndTradeDateBetweenOrderByTradeDateAsc(symbol, start, end);
    }

    @Test
    void testGetStockPrices_NoDates() {
        String symbol = "AAPL";
        when(repository.findBySymbolOrderByTradeDateAsc(symbol)).thenReturn(Collections.emptyList());

        List<StockPrice> result = stockService.getStockPrices(symbol, null, null);

        assertNotNull(result);
        verify(repository).findBySymbolOrderByTradeDateAsc(symbol);
    }

    @Test
    void testSaveFromReader_ValidCsv() {
        String csvContent = "Symbol,Date,Open,High,Low,Close,Volume\n" +
                "AAPL,2-Jan-20,100.0,105.0,99.0,102.0,1000";
        StringReader reader = new StringReader(csvContent);

        // Capture the argument passed to saveAll
        org.mockito.ArgumentCaptor<List<StockPrice>> captor = org.mockito.ArgumentCaptor.forClass(List.class);

        // Mock saveAll to return the list passed to it
        when(repository.saveAll(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Fix: Method returns int count, not List
        int count = stockService.saveFromReader(reader);

        assertEquals(1, count);

        List<StockPrice> captured = captor.getValue();
        StockPrice price = captured.get(0);
        assertEquals("AAPL", price.getSymbol());
        assertEquals(new java.math.BigDecimal("102.0"), price.getClosePrice()); // BigDecimal check
        assertEquals(LocalDate.of(2020, 1, 2), price.getTradeDate());
    }

    @Test
    void testSaveFromReader_TabSeparated() {
        // Tab separated
        String csvContent = "Symbol\tDate\tOpen\tHigh\tLow\tClose\tVolume\n" +
                "GOOG\t2-Jan-20\t500.0\t505.0\t499.0\t502.0\t2000";
        StringReader reader = new StringReader(csvContent);

        org.mockito.ArgumentCaptor<List<StockPrice>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
        when(repository.saveAll(captor.capture())).thenReturn(Collections.emptyList());

        int count = stockService.saveFromReader(reader);

        assertEquals(1, count);
        assertEquals("GOOG", captor.getValue().get(0).getSymbol());
    }

    @Test
    void testDeleteBySymbol() {
        String symbol = "AAPL";
        stockService.deleteBySymbol(symbol);
        verify(repository).deleteBySymbol(symbol);
    }
}
