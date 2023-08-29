package dev.practice.splitpay.api.service.payment.dto;

public record PaymentDto(
        Long from,
        Long to,
        int amount,
        Long requestId
) {
}
