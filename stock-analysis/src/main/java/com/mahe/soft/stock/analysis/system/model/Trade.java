package com.mahe.soft.stock.analysis.system.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Trade {
    private String symbol;
    private String strategyName;
    private Type type; // LONG only for now per requirements

    private LocalDate entryDate;
    private double entryPrice;

    private LocalDate exitDate;
    private double exitPrice;

    private int quantity;

    private double pnl; // Profit and Loss
    private double pnlPercent;

    public enum Type {
        LONG
    }

    public boolean isClosed() {
        return exitDate != null;
    }
}
