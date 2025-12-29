package com.mahe.soft.stock.analysis.system.dsl.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DslStrategyDefinition {
    private String name;
    private List<DslCondition> entryConditions = new ArrayList<>();
    private List<DslCondition> exitConditions = new ArrayList<>();

    // Simplification for V1: Single expression for stop/take profit relative to
    // price
    // e.g. "ATR(14) * 2"
    private DslExpression stopLoss;
    private DslExpression takeProfit;
}
