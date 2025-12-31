package com.mahe.soft.stock.combine.client;

import com.mahe.soft.stock.combine.model.StockPriceDto;
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
        return webClientBuilder
                .baseUrl(stockDbUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // 16MB
                .build()
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/stocks/" + symbol);
                    if (startDate != null) {
                        uriBuilder.queryParam("startDate", startDate);
                    }
                    if (endDate != null) {
                        uriBuilder.queryParam("endDate", endDate);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<StockPriceDto>>() {
                })
                .block();
    }
}
