package com.mahe.soft.stock.combine.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StrategyConfig {
    // Defaults
    @Builder.Default
    private int smaFastPeriod = 50;
    @Builder.Default
    private int smaSlowPeriod = 200;
    @Builder.Default
    private int rsiPeriod = 14;
    @Builder.Default
    private int rsiOverbought = 70;
    @Builder.Default
    private int rsiOversold = 30;

    @Builder.Default
    private int macdFastPeriod = 12;
    @Builder.Default
    private int macdSlowPeriod = 26;
    @Builder.Default
    private int macdSignalPeriod = 9;

    @Builder.Default
    private int bbPeriod = 20;
    @Builder.Default
    private double bbDevUp = 2.0;
    @Builder.Default
    private double bbDevDn = 2.0;
}
