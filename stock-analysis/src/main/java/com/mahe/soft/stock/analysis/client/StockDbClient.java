package com.mahe.soft.stock.analysis.client;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class StockDbClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${stock.db.url:http://localhost:8081}")
    private String stockDbUrl;

    public List<StockPriceDto> getStockPrices(String symbol) {
        return getStockPrices(symbol, null, null);
    }

    public List<StockPriceDto> getStockPrices(String symbol, java.time.LocalDate startDate,
            java.time.LocalDate endDate) {
        return webClientBuilder.codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024)) // 16MB
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(stockDbUrl + "/api/stocks/" + symbol)
                        .queryParamIfPresent("startDate", java.util.Optional.ofNullable(startDate))
                        .queryParamIfPresent("endDate", java.util.Optional.ofNullable(endDate))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<StockPriceDto>>() {
                })
                .block();
    }
}
