package dev.starryeye.stockranker.api.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateStockRequest(
        @NotNull
        Long id,
        @Positive
        BigDecimal price,
        @Positive
        BigDecimal previousClosePrice,
        @Positive
        Long volume,
        @Positive
        Long views
) {
}
