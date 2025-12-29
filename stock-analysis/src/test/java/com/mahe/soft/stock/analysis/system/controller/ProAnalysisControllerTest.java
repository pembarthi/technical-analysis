package com.mahe.soft.stock.analysis.system.controller;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.system.backtest.BacktestEngine;
import com.mahe.soft.stock.analysis.system.backtest.BacktestResult;
import com.mahe.soft.stock.analysis.system.mapper.CandleMapper;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProAnalysisController.class)
class ProAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockDbClient stockDbClient;

    @MockBean
    private BacktestEngine backtestEngine;

    @MockBean
    private CandleMapper candleMapper;

    @MockBean
    private Map<String, TradingStrategy> strategies;

    @Test
    void testRunBacktest() throws Exception {
        TradingStrategy mockStrategy = mock(TradingStrategy.class);
        when(strategies.get("RSI")).thenReturn(mockStrategy);
        when(stockDbClient.getStockPrices(anyString(), any(), any())).thenReturn(Collections.emptyList());
        when(candleMapper.toCandles(any())).thenReturn(Collections.emptyList());
        when(backtestEngine.runBacktest(any(), any(), anyDouble())).thenReturn(BacktestResult.builder().build());

        mockMvc.perform(post("/api/v2/analysis/backtest")
                        .param("symbol", "AAPL")
                        .param("strategyName", "RSI"))
                .andExpect(status().isOk());
    }
}
