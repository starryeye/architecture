package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.api.service.payment.PaymentService;
import dev.practice.splitpay.api.service.settlement.SettlementQueryService;
import dev.practice.splitpay.api.service.settlement.SettlementService;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.domain.settlement.SettlementDetail;
import dev.practice.splitpay.domain.settlement.SettlementDetailStatus;
import dev.practice.splitpay.domain.settlement.SettlementRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaySettlementUseCase {

    private final SettlementService settlementService;
    private final SettlementQueryService settlementQueryService;
    private final PaymentService paymentService;

    public SettlementDetailResponse paySettlement(Long requestId, Long receiverId) {

        SettlementRequest request = settlementQueryService.getSettlementRequestAndDetails(requestId);
        SettlementDetail detail = request.getDetailByReceiverId(receiverId);
        if(detail.getStatus().equals(SettlementDetailStatus.COMPLETED)) {
            throw new IllegalArgumentException("이미 송금 완료하였습니다.");
        }

        paymentService.sendMoney(FacadeFactory.toPaymentDto(request, detail));

        SettlementDetail result = settlementService.paySettlementComplete(requestId, receiverId);

        return SettlementDetailResponse.of(result);
    }
}
