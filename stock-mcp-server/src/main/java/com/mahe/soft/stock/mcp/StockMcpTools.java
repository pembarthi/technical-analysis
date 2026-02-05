package com.mahe.soft.stock.mcp;

import com.mahe.soft.stock.combine.model.CombinedBacktestRequest;
import com.mahe.soft.stock.combine.model.StrategyConfig;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StockMcpTools {

    private final WebClient webClient;

    public StockMcpTools(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @Tool(description = "Run a combined technical analysis backtest for a given stock symbol and strategy configuration")
    public String runCombinedBacktest(String symbol, double capital, StrategyConfig strategyConfig) {
        CombinedBacktestRequest request = new CombinedBacktestRequest();
        request.setSymbol(symbol);
        request.setCapital(capital);
        request.setStrategyConfig(strategyConfig);

        return webClient.post()
                .uri("/api/combined/backtest")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
