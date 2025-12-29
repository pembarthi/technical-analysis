package com.mahe.soft.stock.db.service;

import com.mahe.soft.stock.db.entity.StockPrice;
import com.mahe.soft.stock.db.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class StockService {

    private final StockPriceRepository stockPriceRepository;

    public List<StockPrice> getStockPrices(String symbol, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return stockPriceRepository.findBySymbolAndTradeDateBetweenOrderByTradeDateAsc(symbol, startDate, endDate);
        }
        return stockPriceRepository.findBySymbolOrderByTradeDateAsc(symbol);
    }

    @Transactional
    public int saveFromCsv(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            return saveFromReader(reader);
        } catch (Exception e) {
            log.error("Fail to parse CSV file: " + e.getMessage());
            throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
        }
    }

    @Transactional
    public int saveFromReader(Reader reader) {
        List<StockPrice> stockPrices = new ArrayList<>();
        // Format: d-MMM-yyyy, d-MMM-yy, yyyy-MM-dd
        // Note: Put longer patterns first to avoid partial matching (e.g. 2012 matching
        // yy=20)
        java.time.format.DateTimeFormatter formatter = new java.time.format.DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("[d-MMM-yyyy][d-MMM-yy][yyyy-MM-dd]")
                .toFormatter(java.util.Locale.ENGLISH);

        BufferedReader bufferedReader = (reader instanceof BufferedReader) ? (BufferedReader) reader
                : new BufferedReader(reader);

        try {
            // Detect delimiter
            bufferedReader.mark(4096);
            String firstLine = bufferedReader.readLine();
            char delimiter = '\t'; // Default
            if (firstLine != null) {
                // Heuristic: if comma count > tab count, assume comma
                long commaCount = firstLine.chars().filter(ch -> ch == ',').count();
                long tabCount = firstLine.chars().filter(ch -> ch == '\t').count();
                if (commaCount > tabCount) {
                    delimiter = ',';
                }
            }
            bufferedReader.reset();

            // Build format
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setDelimiter(delimiter)
                    .setIgnoreEmptyLines(true)
                    .build();

            try (CSVParser csvParser = new CSVParser(bufferedReader, format)) {
                for (CSVRecord csvRecord : csvParser) {
                    try {
                        StockPrice stockPrice = new StockPrice();
                        // Support various header names (Case insensitive handling by parser helps, but
                        // mapping checks needed)

                        // Symbol
                        String symbol = null;
                        if (csvRecord.isMapped("Symbol"))
                            symbol = csvRecord.get("Symbol");
                        else if (csvRecord.isMapped("symbol"))
                            symbol = csvRecord.get("symbol");
                        else
                            symbol = csvRecord.get(0); // Fallback to index 0 if headers fail? No, risky.

                        // Actually, if IgnoreHeaderCase is true, get("Symbol") should find "symbol".
                        // BUT if we want to be safe against different namings (e.g. "Ticker" vs
                        // "Symbol") we can add checks.
                        // For now sticking to Symbol/symbol.
                        if (symbol == null && csvRecord.isMapped("Symbol"))
                            symbol = csvRecord.get("Symbol"); // Retrying access

                        // If parser is case insensitive, get("symbol") and get("Symbol") are same key
                        // lookup?
                        // Let's rely on the parser's case insensitivity for standard fields.
                        // But we must handle the Date/trade_date ambiguity.

                        if (csvRecord.isMapped("Symbol"))
                            stockPrice.setSymbol(csvRecord.get("Symbol"));
                        else
                            stockPrice.setSymbol(csvRecord.get("symbol")); // Try lowercase just in case specific
                                                                           // mapping exists

                        String dateStr;
                        if (csvRecord.isMapped("Date"))
                            dateStr = csvRecord.get("Date");
                        else if (csvRecord.isMapped("trade_date"))
                            dateStr = csvRecord.get("trade_date");
                        else
                            dateStr = csvRecord.get(1); // Fallback risk

                        stockPrice.setTradeDate(LocalDate.parse(dateStr, formatter));

                        stockPrice.setOpenPrice(new BigDecimal(getVal(csvRecord, "Open", "open")));
                        stockPrice.setHighPrice(new BigDecimal(getVal(csvRecord, "High", "high")));
                        stockPrice.setLowPrice(new BigDecimal(getVal(csvRecord, "Low", "low")));
                        stockPrice.setClosePrice(new BigDecimal(getVal(csvRecord, "Close", "close")));
                        stockPrice.setVolume(Long.parseLong(getVal(csvRecord, "Volume", "volume")));

                        stockPrices.add(stockPrice);
                    } catch (Exception e) {
                        log.error("Error parsing record in bulk import: {}", csvRecord, e);
                    }
                }
            }
            stockPriceRepository.saveAll(stockPrices);
            log.info("Saved {} records", stockPrices.size());
            return stockPrices.size();

        } catch (Exception e) {
            log.error("Fail to parse CSV content: " + e.getMessage());
            throw new RuntimeException("Fail to parse CSV content: " + e.getMessage());
        }
    }

    private String getVal(CSVRecord record, String... keys) {
        for (String key : keys) {
            if (record.isMapped(key))
                return record.get(key);
        }
        throw new IllegalArgumentException("Column not found: " + String.join(" or ", keys));
    }

    @Transactional
    public void deleteBySymbol(String symbol) {
        stockPriceRepository.deleteBySymbol(symbol);
    }
}
