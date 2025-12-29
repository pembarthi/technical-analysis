package com.mahe.soft.stock.analysis.system.live;

import com.mahe.soft.stock.analysis.system.paper.PaperAccount;
import com.mahe.soft.stock.analysis.system.paper.PaperOrder;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RobinhoodBrokerService implements BrokerService {

    @Override
    public String placeOrder(String symbol, PaperOrder.Side side, int quantity, PaperOrder.Type type) {
        log.info("ROBINHOOD API: Placing Order -> {} {} {} @ {}", side, quantity, symbol, type);
        // Call Robinhood API here
        return UUID.randomUUID().toString();
    }

    @Override
    public double getQuote(String symbol) {
        log.info("ROBINHOOD API: Fetching Quote for {}", symbol);
        // Call Robinhood Data API
        return 150.00; // Mock price
    }

    @Override
    public PaperAccount getAccountSummary() {
        log.info("ROBINHOOD API: Fetching Account Summary");
        // Convert RH response to PaperAccount model
        return PaperAccount.builder()
                .id("RH-LIVE-1234")
                .cashBalance(50000.0)
                .build();
    }
}
