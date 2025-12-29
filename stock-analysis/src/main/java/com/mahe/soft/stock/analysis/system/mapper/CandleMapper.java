package com.mahe.soft.stock.analysis.system.mapper;

import com.mahe.soft.stock.analysis.dto.StockPriceDto;
import com.mahe.soft.stock.analysis.system.model.Candle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CandleMapper {

    public Candle toCandle(StockPriceDto dto) {
        return Candle.builder()
                .timestamp(dto.getTradeDate())
                .open(dto.getOpenPrice().doubleValue())
                .high(dto.getHighPrice().doubleValue())
                .low(dto.getLowPrice().doubleValue())
                .close(dto.getClosePrice().doubleValue())
                .volume(dto.getVolume().doubleValue())
                .build();
    }

    public List<Candle> toCandles(List<StockPriceDto> dtos) {
        return dtos.stream()
                .map(this::toCandle)
                .collect(Collectors.toList());
    }
}
