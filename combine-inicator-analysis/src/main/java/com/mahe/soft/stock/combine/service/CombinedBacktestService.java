package com.mahe.soft.stock.combine.service;

import com.mahe.soft.stock.combine.client.StockDbClient;
import com.mahe.soft.stock.combine.model.Candle;
import com.mahe.soft.stock.combine.model.CombinedBacktestResult;
import com.mahe.soft.stock.combine.model.StockPriceDto;
import com.mahe.soft.stock.combine.service.CombinedStrategyEngine.AnalysisResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CombinedBacktestService {

    private final StockDbClient stockDbClient;
    private final CombinedStrategyEngine strategyEngine;

    public CombinedBacktestResult runBacktest(String symbol, double initialCapital) {
        // 1. Fetch Data
        List<StockPriceDto> prices = stockDbClient.getStockPrices(symbol);
        if (prices.isEmpty())
            throw new RuntimeException("No data found for " + symbol);

        List<Candle> candles = prices.stream().map(p -> Candle.builder()
                .timestamp(p.getTradeDate())
                .open(p.getOpenPrice().doubleValue())
                .high(p.getHighPrice().doubleValue())
                .low(p.getLowPrice().doubleValue())
                .close(p.getClosePrice().doubleValue())
                .volume(p.getVolume())
                .build()).collect(Collectors.toList());

        // 2. Run Strategy Analysis (Bulk)
        AnalysisResult analysis = strategyEngine.runFullAnalysis(candles);

        // 3. Simulate Trading Loop
        double cash = initialCapital;
        double shares = 0;
        double equity = initialCapital;
        int trades = 0;

        // For CSV
        List<String[]> csvRows = new ArrayList<>();
        // Header: Date, Open, High, Low, Close, SMA50, SMA200, RSI, MACD, Signal,
        // Equity

        for (int i = 0; i < candles.size(); i++) {
            Candle c = candles.get(i);
            String signal = analysis.getSignals()[i];
            double price = c.getClose();

            if (signal.equals("BUY") && cash > price) {
                // Buy Max
                double quantity = Math.floor(cash / price);
                if (quantity > 0) {
                    shares += quantity;
                    cash -= quantity * price;
                    trades++;
                }
            } else if (signal.equals("SELL") && shares > 0) {
                // Sell All
                cash += shares * price;
                shares = 0;
                trades++;
            }

            equity = cash + (shares * price);

            // Add Row
            csvRows.add(new String[] {
                    c.getTimestamp().toString(),
                    String.valueOf(c.getOpen()),
                    String.valueOf(c.getHigh()),
                    String.valueOf(c.getLow()),
                    String.valueOf(c.getClose()),
                    String.format("%.2f", analysis.getSma50()[i]),
                    String.format("%.2f", analysis.getSma200()[i]),
                    String.format("%.2f", analysis.getRsi()[i]),
                    String.format("%.2f", analysis.getMacd()[i]),
                    signal,
                    String.format("%.2f", equity)
            });
        }

        // 4. Calculate CAGR
        LocalDate start = candles.get(0).getTimestamp();
        LocalDate end = candles.get(candles.size() - 1).getTimestamp();
        double years = (double) ChronoUnit.DAYS.between(start, end) / 365.25;

        double totalReturn = (equity - initialCapital) / initialCapital;
        double cagr = (years > 0) ? (Math.pow(equity / initialCapital, 1.0 / years) - 1) : 0.0;

        // 5. Generate CSV String
        String csvContent = generateCsv(csvRows);

        return CombinedBacktestResult.builder()
                .symbol(symbol)
                .initialCapital(initialCapital)
                .finalCapital(equity)
                .totalReturnPercent(totalReturn * 100)
                .cagr(cagr * 100)
                .totalTrades(trades)
                .csvContent(csvContent)
                .build();
    }

    private String generateCsv(List<String[]> rows) {
        StringWriter sw = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                .setHeader("Date", "Open", "High", "Low", "Close", "SMA50", "SMA200", "RSI", "MACD", "Signal", "Equity")
                .build())) {
            for (String[] row : rows) {
                printer.printRecord((Object[]) row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generating CSV", e);
        }
        return sw.toString();
    }
}
