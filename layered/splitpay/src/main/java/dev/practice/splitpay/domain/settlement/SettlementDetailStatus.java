package dev.practice.splitpay.domain.settlement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementDetailStatus {

    PENDING("대기 중"),
    REMINDED("리마인드"),
    COMPLETED("완료");

    private final String text;
}
