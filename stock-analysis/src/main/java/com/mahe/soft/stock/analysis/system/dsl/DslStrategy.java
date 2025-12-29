package com.mahe.soft.stock.analysis.system.dsl;

import com.mahe.soft.stock.analysis.service.TALibService;
import com.mahe.soft.stock.analysis.system.dsl.model.*;
import com.mahe.soft.stock.analysis.system.model.Candle;
import com.mahe.soft.stock.analysis.system.model.TradeSignal;
import com.mahe.soft.stock.analysis.system.strategy.TradingStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DslStrategy implements TradingStrategy {

    private final DslStrategyDefinition definition;
    private final TALibService taLibService;

    @Override
    public String getName() {
        return definition.getName();
    }

    @Override
    public TradeSignal evaluate(List<Candle> candles) {
        if (candles.isEmpty())
            return TradeSignal.hold();

        // Evaluate Entry
        if (evaluateConditions(definition.getEntryConditions(), candles)) {
            return TradeSignal.buy(1.0);
        }

        // Evaluate Exit
        if (evaluateConditions(definition.getExitConditions(), candles)) {
            return TradeSignal.sell(1.0);
        }

        return TradeSignal.hold();
    }

    private boolean evaluateConditions(List<DslCondition> conditions, List<Candle> candles) {
        if (conditions.isEmpty())
            return false;

        boolean result = true; // Base for AND

        // First condition sets the base
        result = evaluateSingleCondition(conditions.get(0), candles);

        for (int i = 1; i < conditions.size(); i++) {
            DslCondition cond = conditions.get(i);
            boolean currentVal = evaluateSingleCondition(cond, candles);

            if ("OR".equalsIgnoreCase(cond.getLogicalOperator())) {
                result = result || currentVal;
            } else {
                result = result && currentVal;
            }
        }
        return result;
    }

    private boolean evaluateSingleCondition(DslCondition cond, List<Candle> candles) {
        double leftVal = resolveExpression(cond.getLeft(), candles);
        double rightVal = resolveExpression(cond.getRight(), candles);

        switch (cond.getOperator()) {
            case ">":
                return leftVal > rightVal;
            case "<":
                return leftVal < rightVal;
            case ">=":
                return leftVal >= rightVal;
            case "<=":
                return leftVal <= rightVal;
            case "==":
                return Math.abs(leftVal - rightVal) < 0.0001;
            default:
                return false;
        }
    }

    private double resolveExpression(DslExpression expr, List<Candle> candles) {
        if (expr instanceof ValueExpression) {
            return ((ValueExpression) expr).getValue();
        } else if (expr instanceof IndicatorExpression) {
            return calculateIndicator((IndicatorExpression) expr, candles);
        }
        return 0.0;
    }

    private double calculateIndicator(IndicatorExpression ind, List<Candle> candles) {
        // Extract data needed for indicators
        double[] close = candles.stream().mapToDouble(Candle::getClose).toArray();
        int idx = close.length - 1;

        switch (ind.getName()) {
            case "EMA":
                int period = ind.getParams().get(0).intValue();
                if (close.length < period + 1)
                    return Double.NaN;
                double[] ema = taLibService.ema(close, period);
                return ema[idx];

            case "RSI":
                int rsiPeriod = ind.getParams().get(0).intValue();
                if (close.length < rsiPeriod + 1)
                    return Double.NaN;
                double[] rsi = taLibService.rsi(close, rsiPeriod);
                return rsi[idx];

            case "SMA":
                int smaPeriod = ind.getParams().get(0).intValue();
                double[] sma = taLibService.sma(close, smaPeriod);
                return sma[idx];

            // Add more as needed

            default:
                log.warn("Unknown indicator: {}", ind.getName());
                return Double.NaN;
        }
    }
}
