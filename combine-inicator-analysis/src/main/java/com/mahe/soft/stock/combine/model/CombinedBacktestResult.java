package com.mahe.soft.stock.combine.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CombinedBacktestResult {
    private String symbol;
    private double initialCapital;
    private double finalCapital;
    private double totalReturnPercent;
    private double cagr;
    private int totalTrades;
    private String csvContent; // The generated CSV string
}
