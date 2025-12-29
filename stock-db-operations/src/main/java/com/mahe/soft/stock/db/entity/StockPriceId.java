package com.mahe.soft.stock.db.entity;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceId implements Serializable {
    private String symbol;
    private LocalDate tradeDate;
}
