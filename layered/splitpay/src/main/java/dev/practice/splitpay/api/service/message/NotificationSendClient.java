package dev.practice.splitpay.api.service.message;

public interface NotificationSendClient {

    boolean sendNotification(Long from, Long to, Long requestId, int amount, String content);
}
