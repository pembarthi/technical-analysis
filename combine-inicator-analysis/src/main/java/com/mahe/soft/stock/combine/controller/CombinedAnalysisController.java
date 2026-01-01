package com.mahe.soft.stock.combine.controller;

import com.mahe.soft.stock.combine.model.CombinedBacktestRequest;
import com.mahe.soft.stock.combine.model.CombinedBacktestResult;
import com.mahe.soft.stock.combine.model.StrategyConfig;
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
            @RequestBody CombinedBacktestRequest request) {

        CombinedBacktestResult result = backtestService.runBacktest(request);
        return ResponseEntity.ok(result);
    }

    /*
     * For CSV download, we still support GET but with default config parameters,
     * OR we could accept a limited set of query params to override defaults.
     * To support full config via GET is verbose, so typically we use POST for
     * complex config.
     * Use POST /backtest to get the 'csvContent' string in JSON.
     * This GET endpoint is kept for simple quick downloads with default settings.
     */
    @GetMapping("/backtest/csv")
    public ResponseEntity<byte[]> downloadCsv(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "10000") double capital) {

        CombinedBacktestRequest request = new CombinedBacktestRequest();
        request.setSymbol(symbol);
        request.setCapital(capital);
        request.setStrategyConfig(StrategyConfig.builder().build()); // Defaults

        CombinedBacktestResult result = backtestService.runBacktest(request);
        byte[] csvBytes = result.getCsvContent().getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + symbol + "_backtest.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
