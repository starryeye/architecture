package dev.starryeye.stockranker.api.facade.response;

import java.math.BigDecimal;

public record StockDto(
        Integer rank,
        String code,
        String name,
        BigDecimal price,
        BigDecimal previousClosePrice,
        BigDecimal priceChangeRatio,
        Long volume,
        Long views
) {

}

