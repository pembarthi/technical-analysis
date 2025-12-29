package com.mahe.soft.stock.analysis.service;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import java.util.Arrays;
import org.springframework.stereotype.Service;

/**
 * Service wrapper for TA-Lib Core functions.
 * Includes complete list of Candlestick Pattern Recognition functions.
 */
@Service
public class TALibService {

    private final Core lib = new Core();

    // =========================================================================
    // HELPER: Align output to match input length (pad with NaNs at start)
    // =========================================================================
    private double[] alignResult(double[] out, MInteger begin, int inputLen) {
        double[] aligned = new double[inputLen];
        Arrays.fill(aligned, Double.NaN);
        int outIdx = 0;
        for (int i = begin.value; i < inputLen && outIdx < out.length; i++) {
            aligned[i] = out[outIdx++];
        }
        return aligned;
    }

    private int[] alignIntResult(int[] out, MInteger begin, int inputLen) {
        int[] aligned = new int[inputLen];
        // 0 usually means NO PATTERN, so default 0 is fine.
        int outIdx = 0;
        for (int i = begin.value; i < inputLen && outIdx < out.length; i++) {
            aligned[i] = out[outIdx++];
        }
        return aligned;
    }

    // =========================================================================
    // 1. OVERLAP STUDIES
    // =========================================================================

    public double[] sma(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.sma(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] ema(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.ema(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] wma(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.wma(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] dema(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.dema(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] tema(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.tema(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] trima(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.trima(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] kama(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.kama(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[][] mama(double[] inReal, double fastLimit, double slowLimit) {
        double[] outMama = new double[inReal.length];
        double[] outFama = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.mama(0, inReal.length - 1, inReal, fastLimit, slowLimit, begin, length, outMama, outFama);
        return new double[][] { alignResult(outMama, begin, inReal.length),
                alignResult(outFama, begin, inReal.length) };
    }

    public double[] t3(double[] inReal, int timePeriod, double vFactor) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.t3(0, inReal.length - 1, inReal, timePeriod, vFactor, begin, length, out);
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

    public double[] htTrendline(double[] inReal) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.htTrendline(0, inReal.length - 1, inReal, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] midpoint(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.midPoint(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] sar(double[] inHigh, double[] inLow, double acceleration, double maximum) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.sar(0, inHigh.length - 1, inHigh, inLow, acceleration, maximum, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    // =========================================================================
    // 2. MOMENTUM INDICATORS
    // =========================================================================

    public double[] rsi(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.rsi(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
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

    public double[][] stoch(double[] inHigh, double[] inLow, double[] inClose,
            int fastK_Period, int slowK_Period, MAType slowK_MAType, int slowD_Period, MAType slowD_MAType) {
        double[] outSlowK = new double[inHigh.length];
        double[] outSlowD = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.stoch(0, inHigh.length - 1, inHigh, inLow, inClose, fastK_Period, slowK_Period, slowK_MAType, slowD_Period,
                slowD_MAType, begin, length, outSlowK, outSlowD);
        return new double[][] { alignResult(outSlowK, begin, inHigh.length),
                alignResult(outSlowD, begin, inHigh.length) };
    }

    public double[] adx(double[] inHigh, double[] inLow, double[] inClose, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.adx(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] cci(double[] inHigh, double[] inLow, double[] inClose, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cci(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] mfi(double[] inHigh, double[] inLow, double[] inClose, double[] inVolume, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.mfi(0, inHigh.length - 1, inHigh, inLow, inClose, inVolume, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] mom(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.mom(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] roc(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.roc(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] trix(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.trix(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] willr(double[] inHigh, double[] inLow, double[] inClose, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.willR(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] ultOsc(double[] inHigh, double[] inLow, double[] inClose, int timePeriod1, int timePeriod2,
            int timePeriod3) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.ultOsc(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod1, timePeriod2, timePeriod3, begin, length,
                out);
        return alignResult(out, begin, inHigh.length);
    }

    // =========================================================================
    // 3. VOLATILITY INDICATORS
    // =========================================================================

    public double[] atr(double[] inHigh, double[] inLow, double[] inClose, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.atr(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] natr(double[] inHigh, double[] inLow, double[] inClose, int timePeriod) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.natr(0, inHigh.length - 1, inHigh, inLow, inClose, timePeriod, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] trange(double[] inHigh, double[] inLow, double[] inClose) {
        throw new UnsupportedOperationException("TRANGE not supported in this TA-Lib version");
    }

    // =========================================================================
    // 4. VOLUME INDICATORS
    // =========================================================================

    public double[] ad(double[] inHigh, double[] inLow, double[] inClose, double[] inVolume) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.ad(0, inHigh.length - 1, inHigh, inLow, inClose, inVolume, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    public double[] obv(double[] inReal, double[] inVolume) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.obv(0, inReal.length - 1, inReal, inVolume, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    // =========================================================================
    // 5. CYCLE INDICATORS
    // =========================================================================

    public double[] htDcPeriod(double[] inReal) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.htDcPeriod(0, inReal.length - 1, inReal, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] htDcPhase(double[] inReal) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.htDcPhase(0, inReal.length - 1, inReal, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    // =========================================================================
    // 6. PRICE TRANSFORM
    // =========================================================================

    public double[] avgPrice(double[] inOpen, double[] inHigh, double[] inLow, double[] inClose) {
        double[] out = new double[inOpen.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.avgPrice(0, inOpen.length - 1, inOpen, inHigh, inLow, inClose, begin, length, out);
        return alignResult(out, begin, inOpen.length);
    }

    public double[] medPrice(double[] inHigh, double[] inLow) {
        double[] out = new double[inHigh.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.medPrice(0, inHigh.length - 1, inHigh, inLow, begin, length, out);
        return alignResult(out, begin, inHigh.length);
    }

    // =========================================================================
    // 7. PATTERN RECOGNITION (COMPLETE LIST)
    // =========================================================================

    public int[] cdl2Crows(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdl2Crows(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdl3BlackCrows(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdl3BlackCrows(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdl3Inside(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdl3Inside(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdl3LineStrike(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdl3LineStrike(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdl3Outside(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdl3Outside(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdl3StarsInSouth(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdl3StarsInSouth(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdl3WhiteSoldiers(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdl3WhiteSoldiers(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlAbandonedBaby(double[] open, double[] high, double[] low, double[] close, double penetration) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlAbandonedBaby(0, open.length - 1, open, high, low, close, penetration, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlAdvanceBlock(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlAdvanceBlock(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlBeltHold(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlBeltHold(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlBreakaway(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlBreakaway(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlClosingMarubozu(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlClosingMarubozu(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlConcealBabysWall(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlConcealBabysWall(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlCounterAttack(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlCounterAttack(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlDarkCloudCover(double[] open, double[] high, double[] low, double[] close, double penetration) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlDarkCloudCover(0, open.length - 1, open, high, low, close, penetration, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlDoji(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlDoji(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlDojiStar(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlDojiStar(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlDragonflyDoji(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlDragonflyDoji(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlEngulfing(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlEngulfing(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlEveningDojiStar(double[] open, double[] high, double[] low, double[] close, double penetration) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlEveningDojiStar(0, open.length - 1, open, high, low, close, penetration, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlEveningStar(double[] open, double[] high, double[] low, double[] close, double penetration) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlEveningStar(0, open.length - 1, open, high, low, close, penetration, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlGapSideSideWhite(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlGapSideSideWhite(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlGravestoneDoji(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlGravestoneDoji(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlHammer(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlHammer(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlHangingMan(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlHangingMan(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlHarami(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlHarami(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlHaramiCross(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlHaramiCross(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    // public int[] cdlHighWave(double[] open, double[] high, double[] low, double[]
    // close) {
    // int[] out = new int[open.length]; MInteger begin = new MInteger(); MInteger
    // length = new MInteger();
    // lib.cdlHighWave(0, open.length-1, open, high, low, close, begin, length,
    // out);
    // return alignIntResult(out, begin, open.length);
    // }

    public int[] cdlHikkake(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlHikkake(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlHikkakeMod(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlHikkakeMod(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlHomingPigeon(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlHomingPigeon(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlIdentical3Crows(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlIdentical3Crows(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlInNeck(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlInNeck(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlInvertedHammer(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlInvertedHammer(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlKicking(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlKicking(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlKickingByLength(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlKickingByLength(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlLadderBottom(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlLadderBottom(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlLongLeggedDoji(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlLongLeggedDoji(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlLongLine(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlLongLine(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlMarubozu(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlMarubozu(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlMatchingLow(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlMatchingLow(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlMatHold(double[] open, double[] high, double[] low, double[] close, double penetration) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlMatHold(0, open.length - 1, open, high, low, close, penetration, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlMorningDojiStar(double[] open, double[] high, double[] low, double[] close, double penetration) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlMorningDojiStar(0, open.length - 1, open, high, low, close, penetration, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlMorningStar(double[] open, double[] high, double[] low, double[] close, double penetration) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlMorningStar(0, open.length - 1, open, high, low, close, penetration, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlOnNeck(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlOnNeck(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlPiercing(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlPiercing(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlRickshawMan(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlRickshawMan(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlRiseFall3Methods(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlRiseFall3Methods(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    // public int[] cdlSeparatingLines(double[] open, double[] high, double[] low,
    // double[] close) {
    // int[] out = new int[open.length]; MInteger begin = new MInteger(); MInteger
    // length = new MInteger();
    // lib.cdlSeparatingLines(0, open.length - 1, open, high, low, close, begin,
    // length, out);
    // return alignIntResult(out, begin, open.length);
    // }

    public int[] cdlShootingStar(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlShootingStar(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlShortLine(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlShortLine(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlSpinningTop(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlSpinningTop(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlStalledPattern(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlStalledPattern(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    // public int[] cdlStickSandwich(double[] open, double[] high, double[] low,
    // double[] close) {
    // int[] out = new int[open.length]; MInteger begin = new MInteger(); MInteger
    // length = new MInteger();
    // lib.cdlStickSandwich(0, open.length - 1, open, high, low, close, begin,
    // length, out);
    // return alignIntResult(out, begin, open.length);
    // }

    public int[] cdlTakuri(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlTakuri(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlTasukiGap(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlTasukiGap(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlThrusting(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlThrusting(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlTristar(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlTristar(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlUnique3River(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlUnique3River(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlUpsideGap2Crows(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlUpsideGap2Crows(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    public int[] cdlXSideGap3Methods(double[] open, double[] high, double[] low, double[] close) {
        int[] out = new int[open.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.cdlXSideGap3Methods(0, open.length - 1, open, high, low, close, begin, length, out);
        return alignIntResult(out, begin, open.length);
    }

    // =========================================================================
    // 8. STATISTIC FUNCTIONS
    // =========================================================================

    public double[] beta(double[] inReal0, double[] inReal1, int timePeriod) {
        double[] out = new double[inReal0.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.beta(0, inReal0.length - 1, inReal0, inReal1, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal0.length);
    }

    public double[] correl(double[] inReal0, double[] inReal1, int timePeriod) {
        double[] out = new double[inReal0.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.correl(0, inReal0.length - 1, inReal0, inReal1, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal0.length);
    }

    public double[] linearReg(double[] inReal, int timePeriod) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.linearReg(0, inReal.length - 1, inReal, timePeriod, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }

    public double[] stdDev(double[] inReal, int timePeriod, double nbDev) {
        double[] out = new double[inReal.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        lib.stdDev(0, inReal.length - 1, inReal, timePeriod, nbDev, begin, length, out);
        return alignResult(out, begin, inReal.length);
    }
}
