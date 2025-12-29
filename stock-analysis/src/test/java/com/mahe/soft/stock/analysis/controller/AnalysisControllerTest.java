package com.mahe.soft.stock.analysis.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mahe.soft.stock.analysis.service.BacktestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BacktestService backtestService;

    @Test
    void testRunBacktest() throws Exception {
        when(backtestService.runBacktest(anyString(), anyString())).thenReturn("Success");

        mockMvc.perform(post("/api/analysis/backtest")
                        .param("symbol", "AAPL")
                        .param("strategy", "RSI"))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }
}
