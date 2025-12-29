package com.mahe.soft.stock.analysis.system.paper;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaperOrder {
    public enum Type {
        MARKET, LIMIT
    }

    public enum Side {
        BUY, SELL
    }

    public enum Status {
        PENDING, FILLED, REJECTED
    }

    private String id;
    private String symbol;
    private Side side;
    private Type type;
    private int quantity;
    private double filledPrice;
    private LocalDateTime timestamp;
    private Status status;
}
