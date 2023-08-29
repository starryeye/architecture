package dev.practice.splitpay.api.service.settlement;


import dev.practice.splitpay.IntegrationTestSupport;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.api.facade.response.SettlementRequestAndDetailsResponse;
import dev.practice.splitpay.api.facade.response.SettlementRequestResponse;
import dev.practice.splitpay.domain.settlement.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class SettlementQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private SettlementQueryService settlementQueryService;

    @Autowired
    private SettlementRequestRepository settlementRequestRepository;

    @Autowired
    private SettlementDetailRepository settlementDetailRepository;

    @AfterEach
    void tearDown() {
        settlementDetailRepository.deleteAllInBatch();
        settlementRequestRepository.deleteAllInBatch();
    }

    @DisplayName("주어진 요청자 Id (requesterId) 로 생성된 1/N 정산하기 요청 리스트를 조회한다.")
    @Test
    void getSettlementRequests() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        SettlementRequest settlementRequest1 = createSettlementRequest(requesterId, SettlementRequestStatus.PENDING, registeredAt, null);
        SettlementRequest settlementRequest2 = createSettlementRequest(requesterId, SettlementRequestStatus.COMPLETED, registeredAt, null);
        SettlementRequest settlementRequest3 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, null);
        SettlementRequest settlementRequest4 = createSettlementRequest(2L, SettlementRequestStatus.COMPLETED, registeredAt, null);

        settlementRequestRepository.saveAll(List.of(settlementRequest1, settlementRequest2, settlementRequest3, settlementRequest4));

        // when
        List<SettlementRequest> result = settlementQueryService.getSettlementRequests(requesterId);

        // then
        assertThat(result).hasSize(2)
                .extracting("requesterId", "status", "registeredAt")
                .containsExactly(
                        tuple(requesterId, SettlementRequestStatus.COMPLETED, registeredAt),
                        tuple(requesterId, SettlementRequestStatus.PENDING, registeredAt)
                );
    }

    @DisplayName("주어진 요청자 Id (requesterId) 로 생성된 1/N 정산하기 요청 리스트를 조회할 때, 조회 결과가 없으면 예외 발생")
    @Test
    void getSettlementRequestsButNotFound() {

        // given
        Long requesterId = 99L;

        // when
        // then
        assertThatThrownBy(
                () -> settlementQueryService.getSettlementRequests(requesterId)
        ).isInstanceOf(NoSuchElementException.class)
                .hasMessage("조회 결과가 없습니다.");
    }

    @DisplayName("주어진 요청 Id 로 생성된 1/N 정산하기 요청을 조회한다.(상세정보 포함)")
    @Test
    void getSettlementRequestAndDetails() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail3 = createSettlementDetail(5L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail4 = createSettlementDetail(6L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(1L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));
        SettlementRequest settlementRequest2 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail3, settlementDetail4));

        List<SettlementRequest> savedAll = settlementRequestRepository.saveAll(List.of(settlementRequest1, settlementRequest2));

        Long requestId = savedAll.get(0).getRequestId();

        // when
        SettlementRequest result = settlementQueryService.getSettlementRequestAndDetails(requestId);

        // then
        assertThat(result.getRequestId()).isEqualTo(requestId);
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getTotalAmount()).isEqualTo(2000);
        assertThat(result.getRegisteredAt()).isEqualTo(registeredAt);
        assertThat(result.getStatus()).isEqualTo(SettlementRequestStatus.PENDING);
        assertThat(result.getSettlementDetails()).hasSize(2)
                .extracting("receiverId", "amount", "status")
                .containsExactlyInAnyOrder(
                        tuple(3L, 1000, SettlementDetailStatus.PENDING),
                        tuple(4L, 1000, SettlementDetailStatus.PENDING)
                );
    }

    @DisplayName("주어진 요청 Id 로 생성된 1/N 정산하기 요청을 조회할 때, 조회 결과가 없으면 예외 발생")
    @Test
    void getSettlementRequestButNotFound() {

        // given
        Long requestId = 99L;

        // when
        // then
        assertThatThrownBy(
                () -> settlementQueryService.getSettlementRequestAndDetails(requestId)
        ).isInstanceOf(NoSuchElementException.class)
                .hasMessage("조회 결과가 없습니다.");
    }

    @DisplayName("주어진 정산 요청 대상자(receiverId) 로 등록된 1/N 정산하기 요청의 상세정보 리스트를 조회한다.")
    @Test
    void getSettlementReceives() {

        // given

        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long receiverId = 3L;

        SettlementDetail settlementDetail1 = createSettlementDetail(receiverId, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail3 = createSettlementDetail(receiverId, 1000, SettlementDetailStatus.COMPLETED);
        SettlementDetail settlementDetail4 = createSettlementDetail(5L, 1000, SettlementDetailStatus.COMPLETED);

        SettlementRequest settlementRequest1 = createSettlementRequest(1L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));
        SettlementRequest settlementRequest2 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail3, settlementDetail4));

        settlementRequestRepository.saveAll(List.of(settlementRequest1, settlementRequest2));


        // when
        List<SettlementDetail> result = settlementQueryService.getSettlementDetails(receiverId);

        // then
        assertThat(result).hasSize(2)
                .extracting("receiverId", "amount", "status")
                .containsExactly(
                        tuple(receiverId, 1000, SettlementDetailStatus.COMPLETED),
                        tuple(receiverId, 1000, SettlementDetailStatus.PENDING)
                );
    }

    @DisplayName("주어진 정산 요청 대상자(receiverId) 로 등록된 1/N 정산하기 요청의 상세정보 리스트를 조회할 때, 조회 결과가 없으면 예외 발생")
    @Test
    void getSettlementReceivesButNotFound() {

        // given
        Long receiverId = 99L;

        // when
        // then
        assertThatThrownBy(
                () -> settlementQueryService.getSettlementDetails(receiverId)
        ).isInstanceOf(NoSuchElementException.class)
                .hasMessage("조회 결과가 없습니다.");
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