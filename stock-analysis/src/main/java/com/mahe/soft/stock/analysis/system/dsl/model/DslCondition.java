package com.mahe.soft.stock.analysis.system.dsl.model;

import lombok.Data;

@Data
public class DslCondition {
    private DslExpression left;
    private String operator; // ">", "<", ">=", "<=", "=="
    private DslExpression right;
    private String logicalOperator; // "AND", "OR"
}
