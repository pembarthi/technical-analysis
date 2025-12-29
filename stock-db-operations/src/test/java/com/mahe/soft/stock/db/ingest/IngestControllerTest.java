package com.mahe.soft.stock.db.ingest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IngestController.class)
class IngestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BulkIngestService bulkIngestService;

    @Test
    void testTriggerBulkUpload() throws Exception {
        when(bulkIngestService.processBulkIngestion(anyString())).thenReturn("Success");

        mockMvc.perform(post("/api/stocks/bulk-upload")
                        .param("rootPath", "C:/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }
}
