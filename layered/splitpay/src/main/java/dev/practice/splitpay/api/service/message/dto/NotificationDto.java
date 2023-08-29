package dev.practice.splitpay.api.service.message.dto;

public record NotificationDto(
        Long from,
        Long to,
        Long requestId,
        int amount,
        String content
) {
}
