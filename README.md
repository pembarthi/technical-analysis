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
