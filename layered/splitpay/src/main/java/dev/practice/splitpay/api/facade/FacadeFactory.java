package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.api.service.message.dto.NotificationDto;
import dev.practice.splitpay.api.service.payment.dto.PaymentDto;
import dev.practice.splitpay.domain.settlement.SettlementDetail;
import dev.practice.splitpay.domain.settlement.SettlementRequest;

import java.util.List;

public class FacadeFactory {

    public static List<NotificationDto> toNotificationDto(SettlementRequest request, List<SettlementDetail> details) {
        return details.stream()
                .map(detail -> new NotificationDto(
                        request.getRequesterId(),
                        detail.getReceiverId(),
                        request.getRequestId(),
                        detail.getAmount(),
                        detail.getStatus().getText()
                )).toList();
    }

    public static PaymentDto toPaymentDto(SettlementRequest request, SettlementDetail detail) {
        return new PaymentDto(
                detail.getReceiverId(),
                request.getRequesterId(),
                detail.getAmount(),
                request.getRequestId()
        );
    }
}
