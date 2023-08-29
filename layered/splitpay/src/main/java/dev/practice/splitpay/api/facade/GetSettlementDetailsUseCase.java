package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.api.service.settlement.SettlementQueryService;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSettlementDetailsUseCase {

    private final SettlementQueryService settlementQueryService;

    public List<SettlementDetailResponse> getSettlementDetails(Long receiverId) {

        return settlementQueryService.getSettlementDetails(receiverId).stream()
                .map(SettlementDetailResponse::of)
                .toList();
    }

}
