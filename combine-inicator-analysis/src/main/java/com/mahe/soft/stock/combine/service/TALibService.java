package com.mahe.soft.stock.combine.service;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class TALibService {

    private final Core lib = new Core();

    private double[] alignResult(double[] out, MInteger begin, int inputLen) {
        double[] aligned = new double[inputLen];
        Arrays.fill(aligned, Double.NaN);
        int outIdx = 0;
        for (int i = begin.value; i < inputLen && outIdx < out.length; i++) {
            aligned[i] = out[outIdx++];
        }
        return aligned;
    }

    public double[] ema(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.ema(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] sma(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.sma(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] rsi(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.rsi(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[][] bbands(double[] inReal, int timePeriod, double nbDevUp, double nbDevDn, MAType maType) {
        double[] outUpper = new double[inReal.length];
        double[] outMiddle = new double[inReal.length];
        double[] outLower = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.bbands(0, inReal.length - 1, inReal, timePeriod, nbDevUp, nbDevDn, maType, begin, length, outUpper,
                outMiddle, outLower);
        return new double[][] {
                alignResult(outUpper, begin, inReal.length),
                alignResult(outMiddle, begin, inReal.length),
                alignResult(outLower, begin, inReal.length)
        };
    }

    public double[][] macd(double[] inReal, int fastPeriod, int slowPeriod, int signalPeriod) {
        double[] outMacd = new double[inReal.length];
        double[] outMacdSignal = new double[inReal.length];
        double[] outMacdHist = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.macd(0, inReal.length - 1, inReal, fastPeriod, slowPeriod, signalPeriod, begin, length, outMacd,
                outMacdSignal, outMacdHist);
        return new double[][] {
                alignResult(outMacd, begin, inReal.length),
                alignResult(outMacdSignal, begin, inReal.length),
                alignResult(outMacdHist, begin, inReal.length)
        };
    }

    public double[] adx(double[] inHigh, double[] inLow, double[] inClose, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.adx(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] sar(double[] inHigh, double[] inLow, double acceleration, double maximum) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.sar(0, inHigh.length - 1, inHigh, inLow, acceleration, maximum, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] atr(double[] inHigh, double[] inLow, double[] inClose, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.atr(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }
}
