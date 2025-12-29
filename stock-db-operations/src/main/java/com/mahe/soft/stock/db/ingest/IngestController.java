package com.mahe.soft.stock.db.ingest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Bulk Ingestion", description = "Endpoints for bulk data ingestion")
public class IngestController {

    private final BulkIngestService bulkIngestService;

    @PostMapping("/bulk-upload")
    @Operation(summary = "Trigger Bulk Upload", description = "Scan root directory, unzip files, and ingest csv data.")
    public ResponseEntity<String> triggerBulkUpload(
            @Parameter(description = "Root folder path. Default: C:\\Users\\pemba\\git\\stock-data") @RequestParam(required = false, defaultValue = "C:\\Users\\pemba\\git\\stock-data") String rootPath) {

        // Running in a separate thread might be better for long running tasks, but
        // keeping it synchronous for simplicity as requested "create ingestion process"
        String result = bulkIngestService.processBulkIngestion(rootPath);
        return ResponseEntity.ok(result);
    }
}
