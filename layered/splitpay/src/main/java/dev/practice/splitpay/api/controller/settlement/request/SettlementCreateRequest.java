package dev.practice.splitpay.api.controller.settlement.request;

import dev.practice.splitpay.api.service.settlement.request.SettlementCreateServiceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SettlementCreateRequest {

    @Valid
//    @NotEmpty(message = "1/N 정산하기 요청 대상은 필수입니다.")
    @Size(min = 2, message = "1/N 정산하기 요청 대상은 2명 이상이어야 합니다.")
    private List<SettlementPieceRequest> settlementPieceRequests;

    @Builder
    private SettlementCreateRequest(List<SettlementPieceRequest> settlementPieceRequests) {
        this.settlementPieceRequests = settlementPieceRequests;
    }

    public SettlementCreateServiceRequest toServiceRequest(Long requesterId) {
        return SettlementCreateServiceRequest.builder()
                .requesterId(requesterId)
                .settlementPieceServiceRequests(
                        settlementPieceRequests.stream()
                                .map(SettlementPieceRequest::toServiceRequest)
                                .toList()
                )
                .build();
    }
}
