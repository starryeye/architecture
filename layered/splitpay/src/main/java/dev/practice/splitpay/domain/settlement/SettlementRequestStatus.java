package dev.practice.splitpay.domain.settlement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementRequestStatus {

    PENDING("대기 중"),
    COMPLETED("완료");

    private final String text;
}
