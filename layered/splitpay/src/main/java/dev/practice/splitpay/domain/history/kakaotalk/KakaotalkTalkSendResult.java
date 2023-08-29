package dev.practice.splitpay.domain.history.kakaotalk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KakaotalkTalkSendResult {

    SUCCESS("성공"),
    FAIL("실패");

    private final String text;
}
