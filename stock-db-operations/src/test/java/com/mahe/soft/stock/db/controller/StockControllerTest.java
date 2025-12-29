package com.mahe.soft.stock.db.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mahe.soft.stock.db.service.StockService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    void testGetStockPrices() throws Exception {
        when(stockService.getStockPrices(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/stocks/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testUploadCsvFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "Symbol,Date\nAAPL,2023-01-01".getBytes());
        when(stockService.saveFromCsv(any())).thenReturn(1);

        mockMvc.perform(multipart("/api/stocks/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Saved 1 records")));
    }

    @Test
    void testDeleteStock() throws Exception {
        mockMvc.perform(delete("/api/stocks/AAPL"))
                .andExpect(status().isNoContent());
    }
}
