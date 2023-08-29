package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.IntegrationTestSupport;
import dev.practice.splitpay.api.service.settlement.SettlementQueryService;
import dev.practice.splitpay.domain.settlement.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetSettlementRequestAndDetailsUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private GetSettlementRequestAndDetailsUseCase getSettlementRequestAndDetailsUseCase;

    @Autowired
    private SettlementRequestRepository settlementRequestRepository;

    @Autowired
    private SettlementDetailRepository settlementDetailRepository;

    @AfterEach
    void tearDown() {
        settlementDetailRepository.deleteAllInBatch();
        settlementRequestRepository.deleteAllInBatch();
    }

    @DisplayName("주어진 요청 Id 로 생성된 1/N 정산하기 요청을 조회할 때, 조회된 요청의 요청자 Id 와 주어진 요청자 Id 가 매치되지 않으면 에러 발생한다.")
    @Test
    void getSettlementRequestAndDetailsWithNotMatchRequesterId() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));

        SettlementRequest saved = settlementRequestRepository.save(settlementRequest1);

        Long requestId = saved.getRequestId();

        // when
        // then
        assertThatThrownBy(
                () -> getSettlementRequestAndDetailsUseCase.getSettlementRequestAndDetails(requestId, requesterId)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1/N 정산하기 요청자와 로그인 사용자가 다릅니다.");
    }

    private static SettlementRequest createSettlementRequest(Long requesterId, SettlementRequestStatus status, LocalDateTime registeredAt, List<SettlementDetail> settlementDetails) {
        return SettlementRequest.builder()
                .requesterId(requesterId)
                .status(status)
                .registeredAt(registeredAt)
                .settlementDetails(settlementDetails)
                .build();
    }

    private static SettlementDetail createSettlementDetail(Long receiverId, int amount, SettlementDetailStatus status) {
        return SettlementDetail.builder()
                .receiverId(receiverId)
                .amount(amount)
                .status(status)
                .build();
    }
}