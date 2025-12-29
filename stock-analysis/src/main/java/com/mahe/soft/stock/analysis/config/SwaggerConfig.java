package com.mahe.soft.stock.analysis.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Stock Analysis API", version = "v1", description = "API for Technical Analysis and Backtesting"))
public class SwaggerConfig {
}
