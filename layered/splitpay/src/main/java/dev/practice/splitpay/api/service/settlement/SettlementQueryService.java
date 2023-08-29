package dev.practice.splitpay.api.service.settlement;

import dev.practice.splitpay.domain.settlement.SettlementDetail;
import dev.practice.splitpay.domain.settlement.SettlementDetailRepository;
import dev.practice.splitpay.domain.settlement.SettlementRequest;
import dev.practice.splitpay.domain.settlement.SettlementRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SettlementQueryService {

    private final SettlementRequestRepository settlementRequestRepository;
    private final SettlementDetailRepository settlementDetailRepository;

    public List<SettlementRequest> getSettlementRequests(Long requesterId) {
        List<SettlementRequest> result = settlementRequestRepository.findAllByRequesterIdOrderByCreatedAtDesc(requesterId);

        if(result.isEmpty()) {
            throw new NoSuchElementException("조회 결과가 없습니다.");
        }

        return result;
    }

    public SettlementRequest getSettlementRequestAndDetails(Long requestId) {
        return settlementRequestRepository.findEntityGraphByRequestId(requestId)
                .orElseThrow(() -> new NoSuchElementException("조회 결과가 없습니다."));
    }

    public List<SettlementDetail> getSettlementDetails(Long receiverId) {
        List<SettlementDetail> result = settlementDetailRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId);

        if(result.isEmpty()) {
            throw new NoSuchElementException("조회 결과가 없습니다.");
        }

        return result;
    }
}
