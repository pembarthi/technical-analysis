package com.mahe.soft.stock.analysis.system.live;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.system.mapper.CandleMapper;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.paper.PaperOrder;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveTradingEngine {

    private final BrokerService brokerService;
    private final StockDbClient stockDbClient; // Or use Broker's data feed? Let's use internal DB for strategy analysis
    private final CandleMapper candleMapper;
    private final Map<String, TradingStrategy> strategies;

    // Active Live Strategies: Symbol -> StrategyName
    private final Map<String, String> activeLiveSessions = new ConcurrentHashMap<>();

    public void startLiveTrading(String symbol, String strategyName) {
        if (!strategies.containsKey(strategyName)) {
            throw new IllegalArgumentException("Strategy not found");
        }
        activeLiveSessions.put(symbol, strategyName);
        log.warn("STARTED LIVE TRADING: {} with {}", symbol, strategyName);
    }

    public void stopLiveTrading(String symbol) {
        activeLiveSessions.remove(symbol);
        log.warn("STOPPED LIVE TRADING: {}", symbol);
    }

    @Scheduled(fixedRate = 60000)
    public void runLiveTick() {
        activeLiveSessions.forEach((symbol, strategyName) -> {
            try {
                // 1. Get Data (Mixed approach: Use internal historical + Broker Quote?)
                // For simplicity, using internal DB again
                List<StockPriceDto> prices = stockDbClient.getStockPrices(symbol);
                if (prices.isEmpty())
                    return;

                List<Candle> candles = candleMapper.toCandles(prices);

                TradingStrategy strategy = strategies.get(strategyName);
                TradeSignal signal = strategy.evaluate(candles);

                if (signal.getType() == TradeSignal.Type.BUY) {
                    brokerService.placeOrder(symbol, PaperOrder.Side.BUY, 1, PaperOrder.Type.MARKET);
                } else if (signal.getType() == TradeSignal.Type.SELL) {
                    brokerService.placeOrder(symbol, PaperOrder.Side.SELL, 1, PaperOrder.Type.MARKET);
                }

            } catch (Exception e) {
                log.error("Live Trading Error for {}", symbol, e);
            }
        });
    }
}
