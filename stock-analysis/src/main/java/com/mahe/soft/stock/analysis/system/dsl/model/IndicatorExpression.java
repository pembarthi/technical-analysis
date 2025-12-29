package com.mahe.soft.stock.analysis.system.dsl.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndicatorExpression implements DslExpression {
    private String name; // EMA, RSI
    private List<Double> params; // 14, 21 etc.
}
