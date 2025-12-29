package com.mahe.soft.stock.db.ingest;

import com.mahe.soft.stock.db.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkIngestServiceTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private BulkIngestService bulkIngestService;

    @TempDir
    Path tempDir;

    @Test
    void testProcessBulkIngestion() throws IOException {
        // Create a fake zip file in tempDir
        Path zipPath = tempDir.resolve("test.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            ZipEntry entry = new ZipEntry("data.csv");
            zos.putNextEntry(entry);
            zos.write("Symbol,Date\nAAPL,2023-01-01".getBytes());
            zos.closeEntry();
        }

        when(stockService.saveFromReader(any())).thenReturn(1);

        String result = bulkIngestService.processBulkIngestion(tempDir.toString());

        assertTrue(result.contains("Processed 1 files"));
        assertTrue(result.contains("total 1 records"));
        verify(stockService, atLeastOnce()).saveFromReader(any());
    }

    @Test
    void testProcessBulkIngestion_InvalidPath() {
        String result = bulkIngestService.processBulkIngestion("invalid_path_12345");
        assertTrue(result.contains("does not exist"));
    }
}
