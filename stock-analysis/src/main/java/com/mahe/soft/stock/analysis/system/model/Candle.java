package com.mahe.soft.stock.analysis.system.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Candle {
    private LocalDate timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
}
