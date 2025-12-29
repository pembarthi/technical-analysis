package com.mahe.soft.stock.analysis.system.dsl.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValueExpression implements DslExpression {
    private double value;
}
