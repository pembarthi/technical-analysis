package com.mahe.soft.stock.db.controller;

import com.mahe.soft.stock.db.service.HighValueBacktestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/db-ops")
@RequiredArgsConstructor
public class HighValueBacktestController {

    private final HighValueBacktestService backtestService;

    @PostMapping("/trigger-batch-backtest")
    public ResponseEntity<String> triggerBatchBacktest() {
        // Run async in a separate thread so we return immediately
        new Thread(backtestService::runBatchBacktest).start();
        return ResponseEntity.ok("Batch backtest triggered for high value stocks.");
    }
}
