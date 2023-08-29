package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.api.service.settlement.SettlementQueryService;
import dev.practice.splitpay.api.facade.response.SettlementRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSettlementRequestsUseCase {

    private final SettlementQueryService settlementQueryService;

    public List<SettlementRequestResponse> getSettlementRequests(Long requesterId) {

        return settlementQueryService.getSettlementRequests(requesterId).stream()
                .map(SettlementRequestResponse::of)
                .toList();
    }
}
