package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.api.service.message.NotificationService;
import dev.practice.splitpay.api.service.settlement.SettlementQueryService;
import dev.practice.splitpay.api.service.settlement.SettlementService;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.domain.settlement.SettlementDetail;
import dev.practice.splitpay.domain.settlement.SettlementRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemindSettlementUseCase {

    private final SettlementService settlementService;
    private final SettlementQueryService settlementQueryService;
    private final NotificationService notificationService;

    public List<SettlementDetailResponse> remindSettlement(Long requestId, Long requesterId) {

        SettlementRequest request = settlementQueryService.getSettlementRequestAndDetails(requestId);
        if (request.getRequesterId() != requesterId) {
            throw new IllegalArgumentException("1/N 정산하기 요청자와 로그인 사용자가 다릅니다.");
        }
        List<SettlementDetail> details = request.getFilteredWithoutCompletedDetails();

        notificationService.sendNotificationBulk(FacadeFactory.toNotificationDto(request, details));

        List<SettlementDetail> results = settlementService.remindSettlement(details.stream().map(SettlementDetail::getDetailId).toList());

        return results.stream()
                .map(SettlementDetailResponse::of)
                .toList();
    }
}
