package com.mahe.soft.stock.analysis.system.dsl;

import static org.junit.jupiter.api.Assertions.*;

import com.mahe.soft.stock.analysis.system.dsl.model.DslStrategyDefinition;
import com.mahe.soft.stock.analysis.system.dsl.model.IndicatorExpression;
import com.mahe.soft.stock.analysis.system.dsl.model.ValueExpression;
import org.junit.jupiter.api.Test;

class DslParserTest {

    private final DslParser parser = new DslParser();

    @Test
    void testParse_SimpleStrategy() {
        String script = "STRATEGY TestStrat\n" +
                "ENTRY:\n" +
                "  RSI(14) < 30\n" +
                "EXIT:\n" +
                "  RSI(14) > 70";

        DslStrategyDefinition def = parser.parse(script);

        assertEquals("TestStrat", def.getName());
        assertEquals(1, def.getEntryConditions().size());
        assertEquals(1, def.getExitConditions().size());

        // Check Entry
        var entry = def.getEntryConditions().get(0);
        assertEquals("<", entry.getOperator());
        assertTrue(entry.getLeft() instanceof IndicatorExpression);
        assertEquals("RSI", ((IndicatorExpression) entry.getLeft()).getName());
        assertTrue(entry.getRight() instanceof ValueExpression);
        assertEquals(30.0, ((ValueExpression) entry.getRight()).getValue());
    }

    @Test
    void testParse_CompoundConditions() {
        String script = "STRATEGY TestComp\n" +
                "ENTRY:\n" +
                "  RSI(14) < 30\n" +
                "  AND EMA(20) > 50";

        DslStrategyDefinition def = parser.parse(script);
        assertEquals(2, def.getEntryConditions().size());

        assertEquals("AND", def.getEntryConditions().get(1).getLogicalOperator());
    }
}
