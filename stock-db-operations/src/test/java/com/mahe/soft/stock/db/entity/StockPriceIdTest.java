package com.mahe.soft.stock.db.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StockPriceIdTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDate date = LocalDate.now();
        StockPriceId id1 = new StockPriceId("AAPL", date);
        StockPriceId id2 = new StockPriceId("AAPL", date);
        StockPriceId id3 = new StockPriceId("GOOG", date);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1, id3);
    }

    @Test
    void testGettersAndSetters() {
        StockPriceId id = new StockPriceId();
        id.setSymbol("MSFT");
        id.setTradeDate(LocalDate.of(2023, 1, 1));

        assertEquals("MSFT", id.getSymbol());
        assertEquals(LocalDate.of(2023, 1, 1), id.getTradeDate());
    }
}
