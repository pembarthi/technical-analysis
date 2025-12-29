package com.mahe.soft.stock.analysis.system.dsl;

import com.mahe.soft.stock.analysis.system.dsl.model.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DslParser {

    // Regex for parsing: EMA(14) > 50 or RSI(14) < 30
    // Group 1: Left, Group 2: Op, Group 3: Right
    // Simplified: Don't handle nested parens perfectly, assume simple structure.
    private static final Pattern CONDITION_PATTERN = Pattern.compile("(.+?)\\s*(>|<|>=|<=|==)\\s*(.+)");
    private static final Pattern INDICATOR_PATTERN = Pattern.compile("([A-Z_]+)\\(([^)]+)\\)");

    public DslStrategyDefinition parse(String script) {
        DslStrategyDefinition def = new DslStrategyDefinition();
        String[] lines = script.split("\\r?\\n");

        String currentSection = "";

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#"))
                continue;

            if (line.startsWith("STRATEGY ")) {
                def.setName(line.substring(9).trim());
            } else if (line.endsWith(":")) {
                currentSection = line.replace(":", "").trim();
            } else {
                // Parse rule based on section
                if ("ENTRY".equals(currentSection)) {
                    parseCondition(line, def.getEntryConditions());
                } else if ("EXIT".equals(currentSection)) {
                    parseCondition(line, def.getExitConditions());
                } else if ("STOP_LOSS".equals(currentSection)) {
                    // Logic for stop loss expressions
                } else if ("TAKE_PROFIT".equals(currentSection)) {
                    // Logic for take profit
                }
            }
        }
        return def;
    }

    private void parseCondition(String line, List<DslCondition> conditions) {
        // Check for logical operator prefix (AND/OR) - distinct from internal logic,
        // usually lines are implicitly AND, but prompt showed "AND RSI..."
        String logicalOp = "AND"; // Default
        if (line.startsWith("AND ")) {
            logicalOp = "AND";
            line = line.substring(4).trim();
        } else if (line.startsWith("OR ")) {
            logicalOp = "OR";
            line = line.substring(3).trim();
        }

        Matcher m = CONDITION_PATTERN.matcher(line);
        if (m.find()) {
            String leftStr = m.group(1).trim();
            String op = m.group(2).trim();
            String rightStr = m.group(3).trim();

            DslCondition cond = new DslCondition();
            cond.setLeft(parseExpression(leftStr));
            cond.setOperator(op);
            cond.setRight(parseExpression(rightStr));
            cond.setLogicalOperator(logicalOp);

            conditions.add(cond);
        } else {
            throw new IllegalArgumentException("Invalid condition syntax: " + line);
        }
    }

    private DslExpression parseExpression(String expr) {
        Matcher m = INDICATOR_PATTERN.matcher(expr);
        if (m.matches()) {
            String name = m.group(1);
            String paramsStr = m.group(2);
            List<Double> params = Arrays.stream(paramsStr.split(","))
                    .map(String::trim)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            return new IndicatorExpression(name, params);
        } else {
            try {
                return new ValueExpression(Double.parseDouble(expr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unknown expression format: " + expr);
            }
        }
    }
}
