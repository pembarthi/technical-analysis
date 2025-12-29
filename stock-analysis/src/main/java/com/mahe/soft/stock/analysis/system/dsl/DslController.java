package com.mahe.soft.stock.analysis.system.dsl;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.system.backtest.BacktestEngine;
import com.mahe.soft.stock.analysis.system.backtest.BacktestResult;
import com.mahe.soft.stock.analysis.system.dsl.model.DslStrategyDefinition;
import com.mahe.soft.stock.analysis.system.mapper.CandleMapper;
import com.mahe.soft.stock.analysis.system.model.Candle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/dsl")
@RequiredArgsConstructor
public class DslController {

    private final DslParser dslParser;
    private final BacktestEngine backtestEngine;
    private final StockDbClient stockDbClient;
    private final CandleMapper candleMapper;
    private final TALibService taLibService;

    @PostMapping("/backtest")
    public ResponseEntity<BacktestResult> backtestDsl(
            @RequestParam String symbol,
            @RequestBody String dslScript) {

        // 1. Parsing
        DslStrategyDefinition def = dslParser.parse(dslScript);

        // 2. Compilation (Runtime Strategy)
        DslStrategy strategy = new DslStrategy(def, taLibService);

        // 3. Data Fetching
        List<StockPriceDto> prices = stockDbClient.getStockPrices(symbol);
        List<Candle> candles = candleMapper.toCandles(prices);

        // 4. Execution
        BacktestResult result = backtestEngine.runBacktest(strategy, candles, 10000);
        result.setSymbol(symbol);

        return ResponseEntity.ok(result);
    }
}
