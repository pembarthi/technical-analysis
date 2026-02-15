package com.mahe.soft.stock.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahe.soft.stock.combine.model.CombinedBacktestRequest;
import com.mahe.soft.stock.combine.model.StrategyConfig;
import com.mahe.soft.stock.combine.service.CombinedBacktestService;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public CommandLineRunner mcpServerRunner(CombinedBacktestService backtestService, ObjectMapper objectMapper) {
        return args -> {
            // 1. Create Mapper
            McpJsonMapper mapper = McpJsonMapper.getDefault();

            // 2. Create Transport
            StdioServerTransportProvider transport = new StdioServerTransportProvider(mapper);

            // 3. Define Tool Schema & Tool
            // Properties
            Map<String, Object> symbolProp = Map.of("type", "string", "description", "Stock symbol (e.g., AAPL)");
            Map<String, Object> capitalProp = Map.of("type", "number", "description",
                    "Initial capital (default: 10000)");
            Map<String, Object> strategyConfigProp = Map.of("type", "object", "description",
                    "Strategy configuration parameters");

            Map<String, Object> properties = Map.of(
                    "symbol", symbolProp,
                    "capital", capitalProp,
                    "strategyConfig", strategyConfigProp);

            // JsonSchema constructor: type, properties, required, additionalProperties,
            // defs, definitions
            McpSchema.JsonSchema inputSchema = new McpSchema.JsonSchema(
                    "object",
                    properties,
                    List.of("symbol"),
                    false,
                    null,
                    null);

            // Tool constructor: name, title, description, inputSchema, outputSchema,
            // annotations, meta
            McpSchema.Tool tool = new McpSchema.Tool(
                    "runCombinedBacktest",
                    "runCombinedBacktest", // using name as title if title is required
                    "Run a combined technical analysis backtest for a given stock symbol and strategy configuration",
                    inputSchema,
                    null,
                    null,
                    null);

            // 4. Build Server with Tool
            McpSchema.ServerCapabilities capabilities = McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .logging()
                    .build();

            McpSyncServer server = McpServer.sync(transport)
                    .serverInfo("stock-mcp-server", "1.0.0")
                    .capabilities(capabilities)
                    .tool(tool, (exchange, arguments) -> {
                        try {
                            String symbol = (String) arguments.get("symbol");
                            Double capital = arguments.containsKey("capital")
                                    ? ((Number) arguments.get("capital")).doubleValue()
                                    : 10000.0;

                            StrategyConfig config = null;
                            if (arguments.containsKey("strategyConfig")) {
                                // Using the injected Spring ObjectMapper for complex type conversion
                                // as McpJsonMapper might be limited or we just prefer full Jackson power
                                config = objectMapper.convertValue(arguments.get("strategyConfig"),
                                        StrategyConfig.class);
                            }

                            CombinedBacktestRequest request = new CombinedBacktestRequest();
                            request.setSymbol(symbol);
                            request.setCapital(capital);
                            request.setStrategyConfig(config);

                            var result = backtestService.runBacktest(request);
                            String resultJson = objectMapper.writeValueAsString(result);

                            return new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent(resultJson)),
                                    false);
                        } catch (Exception e) {
                            return new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                                    true);
                        }
                    })
                    .build();

            // 7. Start/Wait
            System.err.println("MCP Server running on STDIO...");

            // Keep the main thread alive. Application formatting/logging should be on
            // stderr.
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
}
