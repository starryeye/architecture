package dev.practice.splitpay.api.facade.response;

import dev.practice.splitpay.domain.settlement.SettlementRequest;
import dev.practice.splitpay.domain.settlement.SettlementRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SettlementRequestResponse {

    private Long requestId;

    private Long requesterId;

    private int totalAmount;

    private SettlementRequestStatus status;

    private int completedCount;

    private LocalDateTime registeredAt;

    @Builder
    private SettlementRequestResponse(Long requestId, Long requesterId, int totalAmount, SettlementRequestStatus status, int completedCount, LocalDateTime registeredAt) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.completedCount = completedCount;
        this.registeredAt = registeredAt;
    }

    public static SettlementRequestResponse of(SettlementRequest settlementRequest) {
        return SettlementRequestResponse.builder()
                .requestId(settlementRequest.getRequestId())
                .requesterId(settlementRequest.getRequesterId())
                .totalAmount(settlementRequest.getTotalAmount())
                .status(settlementRequest.getStatus())
                .completedCount(settlementRequest.getCompletedCount())
                .registeredAt(settlementRequest.getRegisteredAt())
                .build();
    }
}
