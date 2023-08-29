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

    private LocalDateTime registeredAt;

    @Builder
    private SettlementRequestResponse(Long requestId, Long requesterId, int totalAmount, SettlementRequestStatus status, LocalDateTime registeredAt) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public static SettlementRequestResponse of(SettlementRequest settlementRequest) {
        return SettlementRequestResponse.builder()
                .requestId(settlementRequest.getRequestId())
                .requesterId(settlementRequest.getRequesterId())
                .totalAmount(settlementRequest.getTotalAmount())
                .status(settlementRequest.getStatus())
                .registeredAt(settlementRequest.getRegisteredAt())
                .build();
    }
}
