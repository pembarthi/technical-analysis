package com.mahe.soft.stock.db.service;

import com.mahe.soft.stock.db.entity.StockPrice;
import com.mahe.soft.stock.db.repository.StockPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockPriceRepository repository;

    @InjectMocks
    private StockService stockService;

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
}
