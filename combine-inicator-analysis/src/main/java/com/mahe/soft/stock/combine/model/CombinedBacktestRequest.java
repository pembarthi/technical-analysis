package com.mahe.soft.stock.combine.model;

import lombok.Data;

@Data
public class CombinedBacktestRequest {
    private String symbol;
    private double capital = 10000.0;
    private StrategyConfig strategyConfig;
}
