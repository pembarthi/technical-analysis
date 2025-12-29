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
        List<StockPrice> stockPrices = new ArrayList<>();
        // Format: d-MMM-yy (e.g. 2-Jan-12)
        java.time.format.DateTimeFormatter formatter = new java.time.format.DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("[d-MMM-yy][yyyy-MM-dd]")
                .toFormatter(java.util.Locale.ENGLISH);

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            // Support both TDF (Tab) and generic formats by being permissive
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setDelimiter('\t') // Default to tab for user sample
                    .setIgnoreEmptyLines(true)
                    .build();

            try (CSVParser csvParser = new CSVParser(reader, format)) {
                for (CSVRecord csvRecord : csvParser) {
                    try {
                        StockPrice stockPrice = new StockPrice();
                        // Flexible mapping
                        stockPrice.setSymbol(
                                csvRecord.isMapped("Symbol") ? csvRecord.get("Symbol") : csvRecord.get("symbol"));

                        String dateStr = csvRecord.isMapped("Date") ? csvRecord.get("Date")
                                : csvRecord.get("trade_date");
                        stockPrice.setTradeDate(LocalDate.parse(dateStr, formatter));

                        String openStr = csvRecord.isMapped("Open") ? csvRecord.get("Open") : csvRecord.get("open");
                        stockPrice.setOpenPrice(new BigDecimal(openStr));

                        String highStr = csvRecord.isMapped("High") ? csvRecord.get("High") : csvRecord.get("high");
                        stockPrice.setHighPrice(new BigDecimal(highStr));

                        String lowStr = csvRecord.isMapped("Low") ? csvRecord.get("Low") : csvRecord.get("low");
                        stockPrice.setLowPrice(new BigDecimal(lowStr));

                        String closeStr = csvRecord.isMapped("Close") ? csvRecord.get("Close") : csvRecord.get("close");
                        stockPrice.setClosePrice(new BigDecimal(closeStr));

                        String volStr = csvRecord.isMapped("Volume") ? csvRecord.get("Volume")
                                : csvRecord.get("volume");
                        stockPrice.setVolume(Long.parseLong(volStr));

                        stockPrices.add(stockPrice);
                    } catch (Exception e) {
                        log.error("Error parsing record: {}", csvRecord, e);
                    }
                }
            }
            stockPriceRepository.saveAll(stockPrices);
            log.info("Saved {} records", stockPrices.size());
            return stockPrices.size();

        } catch (Exception e) {
            log.error("Fail to parse CSV file: " + e.getMessage());
            throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteBySymbol(String symbol) {
        stockPriceRepository.deleteBySymbol(symbol);
    }
}
