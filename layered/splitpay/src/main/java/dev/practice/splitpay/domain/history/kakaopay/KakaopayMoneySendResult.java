package dev.practice.splitpay.domain.history.kakaopay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KakaopayMoneySendResult {

    SUCCESS("성공"),
    FAIL("실패");

    private final String text;
}
