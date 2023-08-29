package dev.practice.splitpay.api.controller.settlement.request;

import dev.practice.splitpay.api.service.settlement.request.SettlementPieceServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettlementPieceRequest {

    @NotNull(message = "1/N 정산하기 요청 대상 ID는 필수 입니다.")
    private Long receiverId;
    @Positive(message = "1/N 정산하기 요청 금액은 양수여야 합니다.")
    private int amount;

    @Builder
    private SettlementPieceRequest(Long receiverId, int amount) {
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public SettlementPieceServiceRequest toServiceRequest() {
        return SettlementPieceServiceRequest.builder()
                .receiverId(receiverId)
                .amount(amount)
                .build();
    }
}
