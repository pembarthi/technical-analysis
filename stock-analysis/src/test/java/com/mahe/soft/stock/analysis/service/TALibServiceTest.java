package com.mahe.soft.stock.analysis.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TALibServiceTest {

    private final TALibService taLibService = new TALibService();

    @Test
    void testSma() {
        double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] result = taLibService.sma(data, 3);

        assertEquals(data.length, result.length);
        assertTrue(Double.isNaN(result[0]));
        assertTrue(Double.isNaN(result[1]));
        assertEquals(2.0, result[2], 0.001); // (1+2+3)/3
        assertEquals(3.0, result[3], 0.001); // (2+3+4)/3
        assertEquals(9.0, result[9], 0.001); // (8+9+10)/3
    }

    @Test
    void testRsi() {
        double[] data = new double[20];
        for(int i=0; i<20; i++) data[i] = 100 + i;
        double[] result = taLibService.rsi(data, 14);

        assertEquals(20, result.length);
        assertTrue(Double.isNaN(result[13]));
        assertFalse(Double.isNaN(result[14]));
    }
}
