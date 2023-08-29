package dev.practice.splitpay.api.service.settlement.request;

import dev.practice.splitpay.domain.settlement.SettlementDetail;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SettlementPieceServiceRequest {

    private Long receiverId;
    private int amount;

    @Builder
    private SettlementPieceServiceRequest(Long receiverId, int amount) {
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public SettlementDetail toEntity() {
        return SettlementDetail.create(receiverId, amount);
    }
}
