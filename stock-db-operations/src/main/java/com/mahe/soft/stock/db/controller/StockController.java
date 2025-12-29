package com.mahe.soft.stock.db.controller;

import com.mahe.soft.stock.db.entity.StockPrice;
import com.mahe.soft.stock.db.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock Operations", description = "Endpoints for managing stock price data")
public class StockController {

    private final StockService stockService;

    @GetMapping("/{symbol}")
    @Operation(summary = "Get Stock Prices", description = "Retrieve stock prices for a given symbol, optionally filtered by date range")
    public ResponseEntity<List<StockPrice>> getStockPrices(
            @Parameter(description = "Stock Symbol (e.g., AAPL)") @PathVariable String symbol,
            @Parameter(description = "Start Date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End Date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(stockService.getStockPrices(symbol, startDate, endDate));
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload CSV", description = "Upload a CSV file containing stock data. Supports specific formats.")
    @ApiResponse(responseCode = "201", description = "File uploaded and data saved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file or format")
    public ResponseEntity<String> uploadCsvFile(
            @Parameter(description = "CSV File", content = @Content(mediaType = "multipart/form-data")) @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a csv file!");
        }
        try {
            int count = stockService.saveFromCsv(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    "Uploaded the file successfully: " + file.getOriginalFilename() + ". Saved " + count + " records.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload the file: " + file.getOriginalFilename() + "!");
        }
    }

    @DeleteMapping("/{symbol}")
    @Operation(summary = "Delete Stock Data", description = "Delete all stock data for a specific symbol")
    public ResponseEntity<Void> deleteStock(@Parameter(description = "Stock Symbol") @PathVariable String symbol) {
        stockService.deleteBySymbol(symbol);
        return ResponseEntity.noContent().build();
    }
}
