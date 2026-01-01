package com.mahe.soft.stock.combine.service;

import com.mahe.soft.stock.combine.model.Candle;
import com.mahe.soft.stock.combine.model.StrategyConfig;
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
        private double[] smaFast;
        private double[] smaSlow;
        private double[] rsi;
        private double[] macd;
        private double[] macdSignal;
        private double[] bbUpper;
        private double[] bbLower;
        private String[] signals; // BUY, SELL, HOLD
    }

    /*
     * Runs analysis on the entire series at once (Vectorized-style).
     */
    public AnalysisResult runFullAnalysis(List<Candle> candles, StrategyConfig config) {
        int n = candles.size();
        double[] close = candles.stream().mapToDouble(Candle::getClose).toArray();

        // Use defaults if null
        if (config == null)
            config = StrategyConfig.builder().build();

        // 1. Calculate Indicators for entire series using Config
        double[] smaFast = taLib.sma(close, config.getSmaFastPeriod());
        double[] smaSlow = taLib.sma(close, config.getSmaSlowPeriod());
        double[] rsi = taLib.rsi(close, config.getRsiPeriod());

        double[][] macdData = taLib.macd(close,
                config.getMacdFastPeriod(),
                config.getMacdSlowPeriod(),
                config.getMacdSignalPeriod()); // 0=macd, 1=sig, 2=hist

        double[][] bbands = taLib.bbands(close,
                config.getBbPeriod(),
                config.getBbDevUp(),
                config.getBbDevDn(),
                MAType.Ema); // 0=upper, 2=lower

        String[] signals = new String[n];
        // Use longest period for warmup estimation: max(slowMA, slowMacd + signal)
        int warmup = Math.max(config.getSmaSlowPeriod(), config.getMacdSlowPeriod() + config.getMacdSignalPeriod());

        for (int i = 0; i < n; i++) {
            if (i < warmup)
                continue; // Dynamic Warmup

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
                if (rsi[i] < config.getRsiOversold())
                    buyVotes++;
                else if (rsi[i] > config.getRsiOverbought())
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
            if (!Double.isNaN(smaFast[i]) && !Double.isNaN(smaSlow[i])) {
                if (smaFast[i - 1] <= smaSlow[i - 1] && smaFast[i] > smaSlow[i])
                    buyVotes += 2;
                else if (smaFast[i - 1] >= smaSlow[i - 1] && smaFast[i] < smaSlow[i])
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
                .smaFast(smaFast)
                .smaSlow(smaSlow)
                .rsi(rsi)
                .macd(macdData[0])
                .macdSignal(macdData[1])
                .bbUpper(bbands[0])
                .bbLower(bbands[2])
                .signals(signals)
                .build();
    }
}
