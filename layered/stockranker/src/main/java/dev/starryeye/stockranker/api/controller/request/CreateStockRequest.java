package dev.starryeye.stockranker.api.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateStockRequest(
        @NotBlank
        String code,
        @NotBlank
        String name,
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
