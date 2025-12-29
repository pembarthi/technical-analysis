package com.mahe.soft.stock.analysis.controller;

import com.mahe.soft.stock.analysis.service.BacktestService;
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
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "Analysis Operations", description = "Endpoints for technical analysis and backtesting")
public class AnalysisController {

    private final BacktestService backtestService;

    @PostMapping("/backtest")
    @Operation(summary = "Run Backtest", description = "Execute a trading strategy on historical data")
    public ResponseEntity<String> runBacktest(
            @Parameter(description = "Stock Symbol (e.g. AAPL)") @RequestParam String symbol,
            @Parameter(description = "Strategy Name (e.g. 'RSI Strategy')") @RequestParam String strategy) {
        return ResponseEntity.ok(backtestService.runBacktest(symbol, strategy));
    }
}
