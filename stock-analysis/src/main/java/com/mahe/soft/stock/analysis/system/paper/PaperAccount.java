package com.mahe.soft.stock.analysis.system.paper;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PaperAccount {
    private String id;
    private double cashBalance;
    private double initialBalance;
    private List<PaperPosition> positions;
    private List<PaperOrder> orderHistory;

    public double getEquity(double currentPrice) {
        // Simple equity calculation: Cash + Sum(PosValue)
        // Note: This needs careful handling if tracking multiple symbols,
        // passing a map of prices would be better. For now assumes 1 active symbol
        // simulation.
        double posValue = positions.stream()
                .mapToDouble(p -> p.getQuantity() * currentPrice)
                .sum();
        return cashBalance + posValue;
    }
}
