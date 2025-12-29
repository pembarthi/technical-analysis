package com.mahe.soft.stock.analysis.system.live;

import com.mahe.soft.stock.analysis.system.paper.PaperAccount;
import com.mahe.soft.stock.analysis.system.paper.PaperOrder;

public interface BrokerService {
    String placeOrder(String symbol, PaperOrder.Side side, int quantity, PaperOrder.Type type);

    double getQuote(String symbol);

    PaperAccount getAccountSummary();
}
