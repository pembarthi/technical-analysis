package com.mahe.soft.stock.analysis.controller;

import com.mahe.soft.stock.analysis.service.BacktestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final BacktestService backtestService;

    @PostMapping("/backtest")
    public ResponseEntity<String> runBacktest(@RequestParam String symbol, @RequestParam String strategy) {
        return ResponseEntity.ok(backtestService.runBacktest(symbol, strategy));
    }
}
