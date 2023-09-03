package dev.practice.splitpay.api.facade.response;

import dev.practice.splitpay.domain.settlement.SettlementRequest;
import dev.practice.splitpay.domain.settlement.SettlementRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SettlementRequestAndDetailsResponse {

    private Long requestId;

    private Long requesterId;

    private int totalAmount;

    private SettlementRequestStatus status;

    private int completedCount;

    private LocalDateTime registeredAt;

    private List<SettlementDetailResponse> settlementDetails;

    @Builder
    private SettlementRequestAndDetailsResponse(Long requestId, Long requesterId, int totalAmount, SettlementRequestStatus status, int completedCount, LocalDateTime registeredAt, List<SettlementDetailResponse> settlementDetails) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.completedCount = completedCount;
        this.registeredAt = registeredAt;
        this.settlementDetails = settlementDetails;
    }

    public static SettlementRequestAndDetailsResponse of(SettlementRequest settlementRequest) {
        return SettlementRequestAndDetailsResponse.builder()
                .requestId(settlementRequest.getRequestId())
                .requesterId(settlementRequest.getRequesterId())
                .totalAmount(settlementRequest.getTotalAmount())
                .status(settlementRequest.getStatus())
                .completedCount(settlementRequest.getCompletedCount())
                .registeredAt(settlementRequest.getRegisteredAt())
                .settlementDetails(
                        settlementRequest.getSettlementDetails().stream()
                                .map(SettlementDetailResponse::of)
                                .toList()
                )
                .build();
    }
}
