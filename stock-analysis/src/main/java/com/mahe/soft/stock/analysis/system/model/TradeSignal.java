package com.mahe.soft.stock.analysis.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeSignal {
    public enum Type {
        BUY, SELL, HOLD
    }

    private Type type;
    private double confidence; // 0.0 to 1.0

    public static TradeSignal buy(double confidence) {
        return new TradeSignal(Type.BUY, confidence);
    }

    public static TradeSignal sell(double confidence) {
        return new TradeSignal(Type.SELL, confidence);
    }

    public static TradeSignal hold() {
        return new TradeSignal(Type.HOLD, 0.0);
    }
}
