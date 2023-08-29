package dev.practice.splitpay.api.service.settlement;

import dev.practice.splitpay.api.service.settlement.request.SettlementCreateServiceRequest;
import dev.practice.splitpay.api.service.settlement.request.SettlementPieceServiceRequest;
import dev.practice.splitpay.domain.settlement.SettlementDetail;
import dev.practice.splitpay.domain.settlement.SettlementRequest;

import java.time.LocalDateTime;
import java.util.List;

public class SettlementServiceFactory {

    public static SettlementRequest createRequest(SettlementCreateServiceRequest request, LocalDateTime registeredAt) {

        List<SettlementDetail> settlementDetails = request.getSettlementPieceServiceRequests().stream()
                .map(SettlementPieceServiceRequest::toEntity)
                .toList();

        return SettlementRequest.create(
                request.getRequesterId(),
                registeredAt,
                settlementDetails
        );
    }
}
