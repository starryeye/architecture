package dev.practice.splitpay.api.facade.response;

import dev.practice.splitpay.domain.settlement.SettlementDetail;
import dev.practice.splitpay.domain.settlement.SettlementDetailStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SettlementDetailResponse {

    private Long receiverId; // User FK

    private int amount;

    private SettlementDetailStatus status;

    private Long requestId;

    @Builder
    private SettlementDetailResponse(Long receiverId, int amount, SettlementDetailStatus status, Long requestId) {
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.requestId = requestId;
    }

    public static SettlementDetailResponse of(SettlementDetail settlementDetail) {
        return SettlementDetailResponse.builder()
                .receiverId(settlementDetail.getReceiverId())
                .amount(settlementDetail.getAmount())
                .status(settlementDetail.getStatus())
                .requestId(settlementDetail.getSettlementRequest().getRequestId())
                .build();
    }
}
