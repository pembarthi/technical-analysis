package com.mahe.soft.stock.analysis.strategy;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StrategyUtilsTest {

    @Test
    void testGetClosePrices() {
        StockPriceDto p1 = new StockPriceDto(); p1.setClosePrice(new BigDecimal("100.0"));
        StockPriceDto p2 = new StockPriceDto(); p2.setClosePrice(new BigDecimal("105.0"));
        List<StockPriceDto> list = Arrays.asList(p1, p2);

        double[] result = StrategyUtils.getClosePrices(list);
        assertEquals(2, result.length);
        assertEquals(100.0, result[0]);
        assertEquals(105.0, result[1]);
    }
}
