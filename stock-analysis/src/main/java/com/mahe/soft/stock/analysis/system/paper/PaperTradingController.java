package com.mahe.soft.stock.analysis.system.paper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/paper")
@RequiredArgsConstructor
public class PaperTradingController {

    private final PaperTradingEngine engine;

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation(
            @RequestParam String symbol,
            @RequestParam String strategyName,
            @RequestParam(defaultValue = "100000") double initialCapital) {
        String sessionId = engine.startSimulation(symbol, strategyName, initialCapital);
        return ResponseEntity.ok(sessionId);
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopSimulation(@RequestParam String sessionId) {
        engine.stopSimulation(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<PaperAccount> getStatus(@RequestParam String sessionId) {
        return ResponseEntity.ok(engine.getAccountStatus(sessionId));
    }
}
