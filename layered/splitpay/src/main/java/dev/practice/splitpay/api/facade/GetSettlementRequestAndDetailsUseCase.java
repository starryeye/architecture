package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.api.service.settlement.SettlementQueryService;
import dev.practice.splitpay.api.facade.response.SettlementRequestAndDetailsResponse;
import dev.practice.splitpay.domain.settlement.SettlementRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSettlementRequestAndDetailsUseCase {

    private final SettlementQueryService settlementQueryService;

    public SettlementRequestAndDetailsResponse getSettlementRequestAndDetails(Long requestId, Long requesterId) {

        SettlementRequest result = settlementQueryService.getSettlementRequestAndDetails(requestId);

        if(result.getRequesterId() != requesterId) {
            throw new IllegalArgumentException("1/N 정산하기 요청자와 로그인 사용자가 다릅니다.");
        }

        return SettlementRequestAndDetailsResponse.of(result);
    }
}
