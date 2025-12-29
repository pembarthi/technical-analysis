package com.mahe.soft.stock.analysis.system.paper;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

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
