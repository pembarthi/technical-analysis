package com.mahe.soft.stock.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceId implements Serializable {
    private String symbol;
    private LocalDate tradeDate;
}
