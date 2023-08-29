package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.api.service.message.NotificationService;
import dev.practice.splitpay.api.service.settlement.SettlementService;
import dev.practice.splitpay.api.service.settlement.request.SettlementCreateServiceRequest;
import dev.practice.splitpay.api.facade.response.SettlementRequestAndDetailsResponse;
import dev.practice.splitpay.domain.settlement.SettlementRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateSettlementUseCase {

    private final SettlementService settlementService;
    private final NotificationService notificationService;

    @Transactional
    public SettlementRequestAndDetailsResponse createSettlement(SettlementCreateServiceRequest request, LocalDateTime registeredAt) {

        SettlementRequest entity = settlementService.createSettlement(request, registeredAt);

        notificationService.sendNotificationBulk(FacadeFactory.toNotificationDto(entity, entity.getFilteredWithoutCompletedDetails()));

        return SettlementRequestAndDetailsResponse.of(entity);
    }
}
