package com.mahe.soft.stock.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.mahe.soft.stock")
public class StockMcpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockMcpServerApplication.class, args);
    }
}
