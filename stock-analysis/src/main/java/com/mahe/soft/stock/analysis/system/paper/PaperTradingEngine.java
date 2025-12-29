package com.mahe.soft.stock.analysis.system.paper;

import com.mahe.soft.stock.analysis.client.StockDbClient;
import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.system.mapper.CandleMapper;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaperTradingEngine {

    private final StockDbClient stockDbClient;
    private final CandleMapper candleMapper;
    private final Map<String, TradingStrategy> strategies;

    // In-memory session store: SessionID -> AccountState
    private final Map<String, PaperSimulationSession> activeSessions = new ConcurrentHashMap<>();

    @Data
    public static class PaperSimulationSession {
        private String id;
        private String symbol;
        private String strategyName;
        private PaperAccount account;
        private boolean active;
    }

    public String startSimulation(String symbol, String strategyName, double initialCapital) {

        if (!strategies.containsKey(strategyName)) {
            throw new IllegalArgumentException("Strategy not found: " + strategyName);
        }

        String sessionId = UUID.randomUUID().toString();
        PaperAccount account = PaperAccount.builder()
                .id(sessionId)
                .initialBalance(initialCapital)
                .cashBalance(initialCapital)
                .positions(new ArrayList<>())
                .orderHistory(new ArrayList<>())
                .build();

        PaperSimulationSession session = new PaperSimulationSession();
        session.setId(sessionId);
        session.setSymbol(symbol);
        session.setStrategyName(strategyName);
        session.setAccount(account);
        session.setActive(true);

        activeSessions.put(sessionId, session);
        log.info("Started paper trading session {} for {} with {}", sessionId, symbol, strategyName);
        return sessionId;
    }

    public void stopSimulation(String sessionId) {
        PaperSimulationSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.setActive(false);
            log.info("Stopped session {}", sessionId);
        }
    }

    public PaperAccount getAccountStatus(String sessionId) {
        PaperSimulationSession session = activeSessions.get(sessionId);
        if (session == null)
            throw new IllegalArgumentException("Session not found");
        return session.getAccount();
    }

    /**
     * Simulation Tick: Runs periodically to simulate real-time updates.
     * In a real app, this would react to Kafka/Websocket events.
     * Here, we poll the DB for the latest candle and execute logic.
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void runSimulationTick() {
        activeSessions.values().stream().filter(PaperSimulationSession::isActive).forEach(this::processSession);
    }

    private void processSession(PaperSimulationSession session) {
        try {
            // 1. Fetch Latest Data
            List<StockPriceDto> prices = stockDbClient.getStockPrices(session.getSymbol());
            if (prices.isEmpty())
                return;

            List<Candle> candles = candleMapper.toCandles(prices);

            // To be realistic in simulation, we use the LAST candle as "current".
            Candle latestCandle = candles.get(candles.size() - 1);
            double currentPrice = latestCandle.getClose();

            // 2. Evaluate Strategy
            TradingStrategy strategy = strategies.get(session.getStrategyName());
            TradeSignal signal = strategy.evaluate(candles);

            // 3. Execute Orders based on Signal
            PaperAccount account = session.getAccount();

            // Check existing position
            PaperPosition position = account.getPositions().stream()
                    .filter(p -> p.getSymbol().equals(session.getSymbol()))
                    .findFirst()
                    .orElse(null);

            if (signal.getType() == TradeSignal.Type.BUY) {
                // Only buy if no current position and enough cash
                if (position == null && account.getCashBalance() > currentPrice) {

                    int qty = (int) (account.getCashBalance() / currentPrice);
                    if (qty > 0) {
                        double cost = qty * currentPrice;
                        account.setCashBalance(account.getCashBalance() - cost);

                        PaperPosition newPos = PaperPosition.builder()
                                .symbol(session.getSymbol())
                                .entryPrice(currentPrice)
                                .quantity(qty)
                                .currentPrice(currentPrice)
                                .build();
                        account.getPositions().add(newPos);

                        // Record Order
                        account.getOrderHistory().add(PaperOrder.builder()
                                .id(UUID.randomUUID().toString())
                                .symbol(session.getSymbol())
                                .side(PaperOrder.Side.BUY)
                                .type(PaperOrder.Type.MARKET)
                                .status(PaperOrder.Status.FILLED)
                                .quantity(qty)
                                .filledPrice(currentPrice)
                                .timestamp(LocalDateTime.now())
                                .build());

                        log.info("Session {}: EXECUTED BUY {} @ {}", session.getId(), qty, currentPrice);
                    }
                }
            } else if (signal.getType() == TradeSignal.Type.SELL) {
                if (position != null) {
                    // SELL logic
                    double revenue = position.getQuantity() * currentPrice;
                    account.setCashBalance(account.getCashBalance() + revenue);
                    account.getPositions().remove(position); // Close pos

                    // Record Order
                    account.getOrderHistory().add(PaperOrder.builder()
                            .id(UUID.randomUUID().toString())
                            .symbol(session.getSymbol())
                            .side(PaperOrder.Side.SELL)
                            .type(PaperOrder.Type.MARKET)
                            .status(PaperOrder.Status.FILLED)
                            .quantity(position.getQuantity())
                            .filledPrice(currentPrice)
                            .timestamp(LocalDateTime.now())
                            .build());

                    log.info("Session {}: EXECUTED SELL {} @ {}", session.getId(), position.getQuantity(),
                            currentPrice);
                }
            }

            // Update Unrealized PnL for existing positions
            if (position != null) {
                position.setCurrentPrice(currentPrice);
                position.setUnrealizedPnL((currentPrice - position.getEntryPrice()) * position.getQuantity());
            }

        } catch (Exception e) {
            log.error("Error processing session {}", session.getId(), e);
        }
    }
}
