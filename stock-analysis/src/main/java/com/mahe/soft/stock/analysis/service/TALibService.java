package com.mahe.soft.stock.analysis.service;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.springframework.stereotype.Service;

@Service
public class TALibService {

    private final Core taLibCore = new Core();

    public double[] calculateSMA(double[] closePrices, int period) {
        double[] out = new double[closePrices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        RetCode retCode = taLibCore.sma(0, closePrices.length - 1, closePrices, period, begin, length, out);

        if (retCode == RetCode.Success) {
            // The output array might be larger than actual values calculated, trim or
            // handle based on 'begin'
            // TA-Lib output is aligned to the beginning of the output array, but
            // corresponds to input index 'begin'
            return out; // For simplicity returning raw output, consumer knows first (period-1) are
                        // 0/invalid usually
        }
        throw new RuntimeException("TA-Lib SMA calculation failed");
    }

    public double[] calculateRSI(double[] closePrices, int period) {
        double[] out = new double[closePrices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        RetCode retCode = taLibCore.rsi(0, closePrices.length - 1, closePrices, period, begin, length, out);

        if (retCode == RetCode.Success) {
            // returning raw for now
            // Note: TA-Lib returns values starting from index 0 of 'out', which corresponds
            // to index 'begin' of input.
            // A helper to align this to the original array size with NaNs would be better
            // for a real app.
            return out;
        }
        throw new RuntimeException("TA-Lib RSI calculation failed");
    }

    // Add getBeginIndex helper if needed to map result back to dates
}
