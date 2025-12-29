package com.mahe.soft.stock.analysis.system.paper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaperPosition {
    private String symbol;
    private double entryPrice;
    private int quantity;
    private double currentPrice;
    private double unrealizedPnL;
}
