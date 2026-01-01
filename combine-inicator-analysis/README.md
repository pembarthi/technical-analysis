# Combined Indicator Analysis Module

This module implements a robust, "Kitchen Sink" style technical analysis strategy that aggregates signals from multiple technical indicators to form a consensus trading decision. It is designed to filter out noise by requiring confirmation from different types of indicators (Trend, Momentum, Volatility).

## Strategy Logic: The Consensus Engine

The `CombinedStrategyEngine` calculates a "Vote" from each indicator. A final `BUY` or `SELL` signal is generated only if there is a majority consensus.

### Component Indicators

1. **Moving Average Crossover (Trend)**
    * **Logic**: Checks if the Fast SMA crosses above/below the Slow SMA.
    * **Weight**: High (2 Votes). This is the primary trend filter.
    * **Default**: 50-period SMA crossing 200-period ASM (Golden/Death Cross).

2. **MACD (Momentum & Trend)**
    * **Logic**: Checks for MACD Line crossing the Signal Line.
    * **Weight**: Normal (1 Vote).
    * **Default**: Fast(12), Slow(26), Signal(9).

3. **RSI (Momentum / Reversal)**
    * **Logic**: Checks for Overbought (>70) or Oversold (<30) conditions.
    * **Weight**: Normal (1 Vote).
    * **Default**: 14-period RSI.

4. **Bollinger Bands (Volatility / Mean Reversion)**
    * **Logic**: Checks if price pierces the Lower Band (Buy) or Upper Band (Sell).
    * **Weight**: Normal (1 Vote).
    * **Default**: 20-period SMA, 2.0 Standard Deviations.

### Consensus Rules

* **BUY Signal**: Requires `Buy Votes > Sell Votes` AND `Buy Votes >= 2`.
* **SELL Signal**: Requires `Sell Votes > Buy Votes` AND `Sell Votes >= 2`.
* **HOLD**: All other conditions.

---

## API Documentation

### 1. Run Backtest

Executes the combined strategy on historical data and returns performance metrics + CSV data.

**Endpoint:** `POST /api/combined/backtest`

**Request Body (JSON):**

```json
{
  "symbol": "AAPL",
  "capital": 10000.0,
  "strategyConfig": {
    "smaFastPeriod": 50,
    "smaSlowPeriod": 200,
    "rsiPeriod": 14,
    "rsiOverbought": 70,
    "rsiOversold": 30,
    "macdFastPeriod": 12,
    "macdSlowPeriod": 26,
    "macdSignalPeriod": 9,
    "bbPeriod": 20,
    "bbDevUp": 2.0,
    "bbDevDn": 2.0
  }
}
```

**Response:**

```json
{
    "symbol": "AAPL",
    "initialCapital": 10000.0,
    "finalCapital": 15430.50,
    "totalReturnPercent": 54.3,
    "cagr": 12.5,
    "totalTrades": 45,
    "csvContent": "Date,Open,High,Low,Close,SMA50,SMA200,RSI,MACD,Signal,Equity\n..."
}
```

### 2. Download CSV Report

Get the backtest result directly as a downloadable CSV file (uses default strategy settings).

**Endpoint:** `GET /api/combined/backtest/csv?symbol=AAPL&capital=10000`

---

## Architecture

* **Service Layer**: `CombinedStrategyEngine` performs vectorized (bulk) calculations using TA-Lib for high performance.
* **Backtest Integration**: `CombinedBacktestService` handles data fetching (via `stock-db-operations`), trade simulation, and metric calculation (CAGR, Equity Curve).
* **Vectorized Processing**: Indicators are calculated for the entire dataset upfront, ensuring O(N) complexity and allowing efficient CSV generation with all historical indicator values included.
