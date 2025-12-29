package com.mahe.soft.stock.db.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Stock DB Operations API", version = "v1", description = "API for Stock Prices management and Ingestion"))
public class SwaggerConfig {
}
