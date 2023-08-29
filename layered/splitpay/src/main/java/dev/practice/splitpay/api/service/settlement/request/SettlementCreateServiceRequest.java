package dev.practice.splitpay.api.service.settlement.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SettlementCreateServiceRequest {

    private Long requesterId;

    private List<SettlementPieceServiceRequest> settlementPieceServiceRequests;

    @Builder
    private SettlementCreateServiceRequest(Long requesterId, List<SettlementPieceServiceRequest> settlementPieceServiceRequests) {
        this.requesterId = requesterId;
        this.settlementPieceServiceRequests = settlementPieceServiceRequests;
    }
}
