package dev.starryeye.stockranker.api.controller.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRankRequest(
        @NotBlank
        String tag
) {
}
