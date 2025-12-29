# Stock Trading Microservices

A production-ready, multi-module Spring Boot project for stock market data ingestion, technical analysis, and strategy backtesting.

## Modules

### 1. `stock-db-operations`

- **Port**: `8081`
- **Database**: MariaDB
- **Purpose**: Manages stock price data storage and CRUD operations. Supports CSV ingestion.
- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

### 2. `stock-analysis`

- **Port**: `8082`
- **Purpose**: Performs technical analysis (using TA-Lib), defines trading strategies, and runs backtests against the data in `stock-db-operations`.
- **Swagger UI**: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

## Prerequisites

- Java 21+
- Docker & Kubernetes (optional, for containerized run)
- MariaDB (running locally or in K8s)

## Getting Started

### 1. Build the Project

```bash
./gradlew build
```

### 2. Run Database Service

Ensure MariaDB is running `localhost:3306` with user `root`, password `Mahe1336`, and database `mahe_stocks`.

```bash
java -jar stock-db-operations/build/libs/stock-db-operations-0.0.1-SNAPSHOT.jar
```

### 3. Run Analysis Service

```bash
java -jar stock-analysis/build/libs/stock-analysis-0.0.1-SNAPSHOT.jar
```

## API Usage

### Ingest Stock Data (CSV)

Upload a CSV file. The system supports generic headers or the specific format provided in `sample.csv`.

```bash
curl -F "file=@sample.csv" http://localhost:8081/api/stocks/upload
```

### Fetch Stock Data

```bash
curl "http://localhost:8081/api/stocks/AAPL"
```

### Bulk Ingestion (Local Folder)

Trigger a bulk ingestion process that scans a root directory, unzips all `.zip` files into an `unzip` subdirectory, and ingests the CSV data into the database.

```bash
# Default path: C:\Users\pemba\git\stock-data
curl -X POST "http://localhost:8081/api/stocks/bulk-upload?rootPath=C:\Users\pemba\git\stock-data"
```

### Run Backtest

Trigger a backtest for a specific stock and strategy.

```bash
# Available Strategies: "RSI Strategy"
curl -X POST "http://localhost:8082/api/analysis/backtest?symbol=AAPL&strategy=RSI+Strategy"
```

## Trading Strategies

The `stock-analysis` module includes 10 comprehensive strategies powered by TA-Lib:

| Strategy | Description | Indicators Used |
| :--- | :--- | :--- |
| **MA Crossover** | Buy when Fast EMA (12) crosses above Slow EMA (26). Sell on cross below. | EMA |
| **MACD Trend** | Buy when MACD > Signal & Histogram > 0. Sell on bearish cross. | MACD (12, 26, 9) |
| **Bollinger Squeeze** | Trades breakouts from low volatility squeezes (< 5% band width). | BBANDS (20, 2.0) |
| **RSI Mean Reversion** | Buy when RSI < 30 (Oversold) & Price > 200 EMA (Uptrend). | RSI (14), EMA (200) |
| **Candlestick Reversal** | Detects Hammer, Engulfing, Morning/Shooting Star patterns. | CDL Patterns |
| **Breakout + Volume** | Buy when Price breaks 20-day High with > 1.5x Avg Volume. | SMA(Vol), Range |
| **Multi-Indicator** | Strong signals: Price > 200 EMA + RSI [40-60] + Bullish Candle. | EMA, RSI, CDL |
| **ATR Risk** | Demonstrates volatility-based stops/entries using ATR. | ATR (14) |
| **Swing Trading** | Pullback to 21 EMA + RSI > 40 + Bullish Candle. | EMA, RSI, CDL |
| **Win Rate RSI** | A baseline RSI strategy for benchmarking. | RSI (14) |

## Real-Time Trading Workflow

This system works as a robust engine for both backtesting and potential real-time trading:

1. **Data Ingestion**: The `stock-db-operations` service continuously updates price data (e.g., via bulk ingestion or scheduled fetchers).
2. **Strategy Execution**: The `stock-analysis` service fetches the latest `N` candles for a symbol from the DB.
3. **Signal Generation**:
    - The `TradingStrategy` implementations (like `MacdStrategy`) process the list of candles.
    - TA-Lib calculates indicators (e.g., MACD, RSI) on the entire series.
    - The strategy logic evaluates the **latest candle** (and previous contexts) to determine a signal (`BUY`, `SELL`, `HOLD`).
4. **Action**: In a live setup, a `BUY` signal would trigger an order placement to a broker API (e.g., Robinhood, Alpaca), subject to risk checks defined in strategies like `AtrRiskStrategy`.

## Professional Trading Platform (V2)

The system now includes a comprehensive upgraded framework (`com.mahe.soft.stock.analysis.system`) supporting advanced workflows.

### 1. Robust Backtesting

Run backtests with detailed metrics including **Sharpe Ratio**, **Max Drawdown**, and **Profit Factor**.

```bash
# Run Backtest
curl -X POST "http://localhost:8082/api/v2/analysis/backtest?symbol=AAPL&strategyName=EmaCrossoverPro"

# Download CSV Report
curl "http://localhost:8082/api/v2/analysis/backtest/csv?symbol=AAPL&strategyName=EmaCrossoverPro"
```

### 2. Strategy DSL

Define trading strategies dynamically using a text-based syntax. API compiles and executes them on the fly.

**Example Script:**

```text
STRATEGY DynamicScalp
ENTRY:
  RSI(14) < 30
  AND EMA(12) > EMA(26)
EXIT:
  RSI(14) > 70
```

```bash
# Execute DSL Backtest
curl -X POST "http://localhost:8082/api/v2/dsl/backtest?symbol=AAPL" -d "STRATEGY..."
```

### 3. Paper Trading Simulator

Test strategies in a risk-free environment with a virtual account that simulates order fills and tracks PnL in real-time.

```bash
# Start Simulation
curl -X POST "http://localhost:8082/api/v2/paper/start?symbol=AAPL&strategyName=MacdTrendPro&initialCapital=100000"

# Check Status
curl "http://localhost:8082/api/v2/paper/status?sessionId=..."
```

### 4. Live Trading Integration

Ready for live execution with a pluggable `BrokerService`. Includes a pre-configured **Robinhood** stub.

```bash
# Start Live Trading
curl -X POST "http://localhost:8082/api/v2/live/start?symbol=AAPL&strategyName=EmaCrossoverPro"
```

## Infrastructure

- **Docker**: Dockerfiles are located in each module directory.
- **Kubernetes**: Manifests in `k8s/` folder for DB, App, and Analysis.

## Sample Data

A `sample.csv` is included in the root directory for testing. It follows the format:

```csv
Symbol Date Open High Low Close Volume
ACU 2-Jan-12 9.5 9.5 9.5 9.5 0
```

Note: Tab-separated values are supported.
