package com.mahe.soft.stock.db.repository;

import com.mahe.soft.stock.db.entity.StockPrice;
import com.mahe.soft.stock.db.entity.StockPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, StockPriceId> {
    List<StockPrice> findBySymbolAndTradeDateBetweenOrderByTradeDateAsc(String symbol, LocalDate startDate,
            LocalDate endDate);

    List<StockPrice> findBySymbolOrderByTradeDateAsc(String symbol);

    void deleteBySymbol(String symbol);
}
