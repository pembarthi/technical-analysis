package com.mahe.soft.stock.db.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahe.soft.stock.db.repository.StockPriceRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighValueBacktestService {

    private final StockPriceRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public void runBatchBacktest() {
        log.info("Starting batch backtest for high value stocks...");
        List<String> symbols = repository.findHighValueSymbols();
        log.info("Found {} symbols with close price > 100", symbols.size());

        for (String symbol : symbols) {
            try {
                triggerBacktest(symbol);
            } catch (Exception e) {
                log.error("Failed to trigger backtest for {}", symbol, e);
            }
        }
        log.info("Batch backtest completed.");
    }

    private void triggerBacktest(String symbol) {
        String payload = createPayload(symbol);

        String responseJson = webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/api/combined/backtest")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (responseJson != null) {
            try {
                BacktestResponse backtestResponse = objectMapper.readValue(responseJson, BacktestResponse.class);
                log.info("Backtest result for {}: CAGR = {}", symbol, backtestResponse.getCagr());

                if (backtestResponse.getCagr() > 25) {
                    writeCsvToFile(symbol, backtestResponse.getCsvContent());
                }
            } catch (JsonProcessingException e) {
                log.error("Error parsing response for {}", symbol, e);
            }
        }

        log.info("Backtest triggered for {}: Response length {}", symbol, responseJson != null ? responseJson.length() : 0);
    }

    private void writeCsvToFile(String symbol, String csvContent) {
        try {
            Path path = Paths.get(symbol + ".csv");
            Files.writeString(path, csvContent);
            log.info("CSV file written for symbol: {}.csv", symbol);
        } catch (IOException e) {
            log.error("Failed to write CSV file for symbol: {}", symbol, e);
        }
    }

    private String createPayload(String symbol) {
        // Hardcoded payload structure as requested
        // Using a Map or simple string replacement might be easier than a full DTO if
        // we don't want to duplicate DTOs
        // But let's look at the structure requested.

        /*
         * {
         * "symbol": "TSLA",
         * "capital": 10000,
         * "strategyConfig": { ... }
         * }
         */

        // We will construct it manually or use a local inner class DTO to ensure valid
        // JSON
        CombinedBacktestRequest request = CombinedBacktestRequest.builder()
                .symbol(symbol)
                .capital(10000)
                .strategyConfig(StrategyConfig.builder()
                        .smaFastPeriod(20)
                        .smaSlowPeriod(50)
                        .rsiPeriod(14)
                        .rsiOverbought(70)
                        .rsiOversold(30)
                        .macdFastPeriod(12)
                        .macdSlowPeriod(26)
                        .macdSignalPeriod(9)
                        .bbPeriod(20)
                        .bbDevUp(2.0)
                        .bbDevDn(2.0)
                        .build())
                .build();

        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating payload", e);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class BacktestResponse {
        private String symbol;
        private double initialCapital;
        private double finalCapital;
        private double totalReturnPercent;
        private double cagr;
        private int totalTrades;
        private String csvContent;
    }

    @Data
    @Builder
    static class CombinedBacktestRequest {
        private String symbol;
        private double capital;
        private StrategyConfig strategyConfig;
    }

    @Data
    @Builder
    static class StrategyConfig {
        private int smaFastPeriod;
        private int smaSlowPeriod;
        private int rsiPeriod;
        private int rsiOverbought;
        private int rsiOversold;
        private int macdFastPeriod;
        private int macdSlowPeriod;
        private int macdSignalPeriod;
        private int bbPeriod;
        private double bbDevUp;
        private double bbDevDn;
    }
}
