package com.mahe.soft.stock.combine.controller;

import com.mahe.soft.stock.combine.model.CombinedBacktestResult;
import com.mahe.soft.stock.combine.service.CombinedBacktestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/combined")
@RequiredArgsConstructor
public class CombinedAnalysisController {

    private final CombinedBacktestService backtestService;

    @PostMapping("/backtest")
    public ResponseEntity<CombinedBacktestResult> runBacktest(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "10000") double capital) {

        CombinedBacktestResult result = backtestService.runBacktest(symbol, capital);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/backtest/csv")
    public ResponseEntity<byte[]> downloadCsv(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "10000") double capital) {

        CombinedBacktestResult result = backtestService.runBacktest(symbol, capital);
        byte[] csvBytes = result.getCsvContent().getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + symbol + "_backtest.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
