package com.mahe.soft.stock.analysis.client;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StockDbClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${stock.db.url:http://localhost:8081}")
    private String stockDbUrl;

    public List<StockPriceDto> getStockPrices(String symbol) {
        return webClientBuilder.build()
                .get()
                .uri(stockDbUrl + "/api/stocks/" + symbol)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<StockPriceDto>>() {
                })
                .block(); // Blocking for simplicity in this phase, preferably reactive down the line
    }
}
