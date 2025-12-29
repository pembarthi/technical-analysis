package com.mahe.soft.stock.db.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StockPriceTest {

    @Test
    void testStockPriceCreation() {
        LocalDate date = LocalDate.of(2023, 1, 1);
        BigDecimal open = new BigDecimal("150.00");
        BigDecimal high = new BigDecimal("155.00");
        BigDecimal low = new BigDecimal("149.00");
        BigDecimal close = new BigDecimal("153.00");
        Long volume = 1000000L;

        StockPrice stockPrice = new StockPrice("AAPL", date, open, high, low, close, volume);

        assertEquals("AAPL", stockPrice.getSymbol());
        assertEquals(date, stockPrice.getTradeDate());
        assertEquals(open, stockPrice.getOpenPrice());
        assertEquals(high, stockPrice.getHighPrice());
        assertEquals(low, stockPrice.getLowPrice());
        assertEquals(close, stockPrice.getClosePrice());
        assertEquals(volume, stockPrice.getVolume());
    }
}
