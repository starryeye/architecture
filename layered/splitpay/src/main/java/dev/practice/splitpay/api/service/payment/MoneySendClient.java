package dev.practice.splitpay.api.service.payment;

public interface MoneySendClient {

    boolean sendMoney(Long from, Long to, int amount, Long requestId);
}
