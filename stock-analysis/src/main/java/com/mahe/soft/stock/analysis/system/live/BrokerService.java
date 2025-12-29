package com.mahe.soft.stock.analysis.system.live;

import com.mahe.soft.stock.analysis.system.paper.PaperOrder; // Reusing model for simplicity or creating new DTO
import com.mahe.soft.stock.analysis.system.paper.PaperAccount;

public interface BrokerService {
    String placeOrder(String symbol, PaperOrder.Side side, int quantity, PaperOrder.Type type);

    double getQuote(String symbol);

    PaperAccount getAccountSummary();
}
