package com.mahe.soft.stock.analysis.system.backtest;

import com.mahe.soft.stock.analysis.system.model.Trade;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BacktestResult {
    private String strategyName;
    private String symbol;

    // Performance Metrics
    private double totalReturnPercent;
    private double winRate; // 0.0 to 1.0
    private double maxDrawdownPercent;
    private double sharpeRatio;
    private double profitFactor;

    private int totalTrades;
    private int winningTrades;
    private int losingTrades;

    private List<Trade> trades;
    private double initialCapital;
    private double finalCapital;
}
