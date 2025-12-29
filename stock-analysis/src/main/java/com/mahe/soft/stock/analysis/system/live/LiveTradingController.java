package com.mahe.soft.stock.analysis.system.live;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/live")
@RequiredArgsConstructor
public class LiveTradingController {

    private final LiveTradingEngine liveEngine;

    @PostMapping("/start")
    public ResponseEntity<String> startLive(
            @RequestParam String symbol,
            @RequestParam String strategyName) {
        liveEngine.startLiveTrading(symbol, strategyName);
        return ResponseEntity.ok("Live trading started for " + symbol);
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopLive(@RequestParam String symbol) {
        liveEngine.stopLiveTrading(symbol);
        return ResponseEntity.ok("Live trading stopped for " + symbol);
    }
}
