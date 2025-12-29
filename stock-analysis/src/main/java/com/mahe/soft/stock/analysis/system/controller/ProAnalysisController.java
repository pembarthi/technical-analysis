package com.mahe.soft.stock.analysis.system.controller;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.system.backtest.BacktestEngine;
import com.mahe.soft.stock.analysis.system.backtest.BacktestResult;
import com.mahe.soft.stock.analysis.system.mapper.CandleMapper;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/analysis")
@RequiredArgsConstructor
public class ProAnalysisController {

    private final StockDbClient stockDbClient;
    private final BacktestEngine backtestEngine;
    private final CandleMapper candleMapper;
    private final Map<String, TradingStrategy> strategies; // Spring injects all beans of type TradingStrategy

    @PostMapping("/backtest")
    public ResponseEntity<BacktestResult> runBacktest(
            @RequestParam String symbol,
            @RequestParam String strategyName,
            @RequestParam(defaultValue = "10000") double capital,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        TradingStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy not found: " + strategyName);
        }

        List<StockPriceDto> prices = stockDbClient.getStockPrices(symbol, from, to);
        List<Candle> candles = candleMapper.toCandles(prices);

        BacktestResult result = backtestEngine.runBacktest(strategy, candles, capital);
        result.setSymbol(symbol);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/backtest/csv")
    public ResponseEntity<String> runBacktestCsv(
            @RequestParam String symbol,
            @RequestParam String strategyName,
            @RequestParam(defaultValue = "10000") double capital) {

        TradingStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            return ResponseEntity.badRequest().body("Strategy not found");
        }

        List<StockPriceDto> prices = stockDbClient.getStockPrices(symbol, null, null);
        List<Candle> candles = candleMapper.toCandles(prices);

        BacktestResult result = backtestEngine.runBacktest(strategy, candles, capital);

        StringBuilder csv = new StringBuilder();
        csv.append("Symbol,Strategy,Total Trades,Win Rate,Total Return %,Sharpe Ratio,Profit Factor\n");
        csv.append(String.format("%s,%s,%d,%.2f,%.2f,%.2f,%.2f\n",
                symbol, strategyName, result.getTotalTrades(), result.getWinRate(),
                result.getTotalReturnPercent(), result.getSharpeRatio(), result.getProfitFactor()));

        csv.append("\nTrade Log:\n");
        csv.append("Type,Entry Date,Entry Price,Exit Date,Exit Price,Qty,PnL\n");

        result.getTrades().forEach(t -> {
            csv.append(String.format("%s,%s,%.2f,%s,%.2f,%d,%.2f\n",
                    t.getType(), t.getEntryDate(), t.getEntryPrice(),
                    t.getExitDate(), t.getExitPrice(), t.getQuantity(), t.getPnl()));
        });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backtest_" + symbol + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }
}
