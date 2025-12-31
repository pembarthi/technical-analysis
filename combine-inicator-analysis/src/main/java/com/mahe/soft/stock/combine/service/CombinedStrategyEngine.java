package com.mahe.soft.stock.combine.service;

import com.mahe.soft.stock.combine.model.Candle;
import com.tictactec.ta.lib.MAType;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CombinedStrategyEngine {

    private final TALibService taLib;

    @Data
    @Builder
    public static class AnalysisResult {
        private double[] close;
        private double[] sma50;
        private double[] sma200;
        private double[] rsi;
        private double[] macd;
        private double[] macdSignal;
        private double[] bbUpper;
        private double[] bbLower;
        private String[] signals; // BUY, SELL, HOLD
    }

    /*
     * Runs analysis on the entire series at once (Vectorized-style).
     * This is much more efficient for backtesting and allows generating the CSV
     * with all indicator values.
     */
    public AnalysisResult runFullAnalysis(List<Candle> candles) {
        int n = candles.size();
        double[] close = candles.stream().mapToDouble(Candle::getClose).toArray();

        // 1. Calculate Indicators for entire series
        double[] sma50 = taLib.sma(close, 50);
        double[] sma200 = taLib.sma(close, 200);
        double[] rsi = taLib.rsi(close, 14);
        double[][] macdData = taLib.macd(close, 12, 26, 9); // 0=macd, 1=sig, 2=hist
        double[][] bbands = taLib.bbands(close, 20, 2.0, 2.0, MAType.Ema); // 0=upper, 2=lower

        String[] signals = new String[n];
        for (int i = 0; i < n; i++) {
            signals[i] = "HOLD"; // Default
            if (i < 50)
                continue; // Warmup

            // Consensus Vote for index i
            int buyVotes = 0;
            int sellVotes = 0;

            // MACD
            if (!Double.isNaN(macdData[0][i]) && !Double.isNaN(macdData[1][i])) {
                if (macdData[0][i - 1] <= macdData[1][i - 1] && macdData[0][i] > macdData[1][i])
                    buyVotes++;
                else if (macdData[0][i - 1] >= macdData[1][i - 1] && macdData[0][i] < macdData[1][i])
                    sellVotes++;
            }

            // RSI
            if (!Double.isNaN(rsi[i])) {
                if (rsi[i] < 30)
                    buyVotes++;
                else if (rsi[i] > 70)
                    sellVotes++;
            }

            // BB
            if (!Double.isNaN(bbands[0][i])) {
                if (close[i] < bbands[2][i])
                    buyVotes++; // Lower band
                else if (close[i] > bbands[0][i])
                    sellVotes++; // Upper band
            }

            // MA Cross (Weighted higher)
            if (!Double.isNaN(sma50[i]) && !Double.isNaN(sma200[i])) {
                if (sma50[i - 1] <= sma200[i - 1] && sma50[i] > sma200[i])
                    buyVotes += 2;
                else if (sma50[i - 1] >= sma200[i - 1] && sma50[i] < sma200[i])
                    sellVotes += 2;
            }

            // Simple Consensus Threshold
            if (buyVotes > sellVotes && buyVotes >= 2)
                signals[i] = "BUY";
            else if (sellVotes > buyVotes && sellVotes >= 2)
                signals[i] = "SELL";
        }

        return AnalysisResult.builder()
                .close(close)
                .sma50(sma50)
                .sma200(sma200)
                .rsi(rsi)
                .macd(macdData[0])
                .macdSignal(macdData[1])
                .bbUpper(bbands[0])
                .bbLower(bbands[2])
                .signals(signals)
                .build();
    }
}
