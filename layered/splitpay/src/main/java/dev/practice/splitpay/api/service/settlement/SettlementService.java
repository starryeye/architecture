package dev.practice.splitpay.api.service.settlement;

import dev.practice.splitpay.api.service.RetryOnOptimisticLocking;
import dev.practice.splitpay.api.service.settlement.request.SettlementCreateServiceRequest;
import dev.practice.splitpay.domain.settlement.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRequestRepository settlementRequestRepository;
    private final SettlementDetailRepository settlementDetailRepository;

    public SettlementRequest createSettlement(SettlementCreateServiceRequest request, LocalDateTime registeredAt) {

        SettlementRequest entity = SettlementServiceFactory.createRequest(request, registeredAt);
        settlementRequestRepository.save(entity);
        return entity;
    }

    @RetryOnOptimisticLocking
    public SettlementDetail paySettlementComplete(Long requestId, Long receiverId) {

        SettlementRequest request = settlementRequestRepository.findEntityGraphByRequestId(requestId).orElseThrow();

        request.updateDetailsStatus(List.of(receiverId), SettlementDetailStatus.COMPLETED);
        request.increaseCompletedCount(); //TODO: 응답 DTO, Optimistic Lock test

        request.checkCompleteStatus(); //TODO : 삭제 고려

        return request.getDetailByReceiverId(receiverId);
    }

    public List<SettlementDetail> remindSettlement(List<Long> detailIds) {

        List<SettlementDetail> allById = settlementDetailRepository.findAllById(detailIds);

        allById.forEach(detail -> detail.updateStatus(SettlementDetailStatus.REMINDED));

        return allById;
    }
}
