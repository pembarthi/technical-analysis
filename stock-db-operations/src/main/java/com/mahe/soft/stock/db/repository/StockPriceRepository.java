package com.mahe.soft.stock.db.repository;

import com.mahe.soft.stock.db.entity.StockPrice;
import com.mahe.soft.stock.db.entity.StockPriceId;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, StockPriceId> {
    List<StockPrice> findBySymbolAndTradeDateBetweenOrderByTradeDateAsc(String symbol, LocalDate startDate,
            LocalDate endDate);

    List<StockPrice> findBySymbolOrderByTradeDateAsc(String symbol);

    void deleteBySymbol(String symbol);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT symbol FROM stock_prices WHERE close_price > 50", nativeQuery = true)
    List<String> findHighValueSymbols();
}
