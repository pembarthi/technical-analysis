package com.mahe.soft.stock.analysis.strategy.impl;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.strategy.TradeSignal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RsiStrategyTest {

    @Mock
    private TALibService taLibService;

    @InjectMocks
    private RsiStrategy rsiStrategy;

    @Test
    void testAnalyze_BuySignal() {
        List<StockPriceDto> prices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            StockPriceDto p = new StockPriceDto();
            p.setClosePrice(new BigDecimal("100"));
            prices.add(p);
        }

        double[] rsiValues = new double[20];
        rsiValues[19] = 25.0; // Overbought < 30

        when(taLibService.rsi(any(), eq(14))).thenReturn(rsiValues);

        TradeSignal signal = rsiStrategy.analyze(prices);
        assertEquals(TradeSignal.BUY, signal);
    }

    @Test
    void testAnalyze_SellSignal() {
        List<StockPriceDto> prices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            StockPriceDto p = new StockPriceDto();
            p.setClosePrice(new BigDecimal("100"));
            prices.add(p);
        }

        double[] rsiValues = new double[20];
        rsiValues[19] = 75.0; // Oversold > 70

        when(taLibService.rsi(any(), eq(14))).thenReturn(rsiValues);

        TradeSignal signal = rsiStrategy.analyze(prices);
        assertEquals(TradeSignal.SELL, signal);
    }
}
