package dev.practice.splitpay.api.service.settlement;

import dev.practice.splitpay.IntegrationTestSupport;
import dev.practice.splitpay.api.service.settlement.request.SettlementCreateServiceRequest;
import dev.practice.splitpay.api.service.settlement.request.SettlementPieceServiceRequest;
import dev.practice.splitpay.domain.settlement.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class SettlementServiceTest extends IntegrationTestSupport {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SettlementRequestRepository settlementRequestRepository;

    @Autowired
    private SettlementDetailRepository settlementDetailRepository;

    @AfterEach
    void tearDown() {
        settlementDetailRepository.deleteAllInBatch();
        settlementRequestRepository.deleteAllInBatch();
    }

    @DisplayName("1/N 정산하기 요청을 생성하고 DB에 등록하고, 반환한다.")
    @Test
    @Transactional
    void createSettlement() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        SettlementPieceServiceRequest settlementPiece1 = createSettlementPiece(2L, 1000);
        SettlementPieceServiceRequest settlementPiece2 = createSettlementPiece(3L, 2000);
        SettlementPieceServiceRequest settlementPiece3 = createSettlementPiece(4L, 3000);

        SettlementCreateServiceRequest request = createSettlementCreate(1L, List.of(settlementPiece1, settlementPiece2, settlementPiece3));

        // when
        SettlementRequest result = settlementService.createSettlement(request, registeredAt);

        // then
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getTotalAmount()).isEqualTo(6000);
        assertThat(result.getRegisteredAt()).isEqualTo(registeredAt);
        assertThat(result.getSettlementDetails()).hasSize(3)
                .extracting("receiverId", "amount")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1000),
                        tuple(3L, 2000),
                        tuple(4L, 3000)
                );

        List<SettlementRequest> all = settlementRequestRepository.findAll();
        SettlementRequest findBy = all.get(0);
        assertThat(findBy.getRequesterId()).isEqualTo(1L);
        assertThat(findBy.getTotalAmount()).isEqualTo(6000);
        assertThat(findBy.getRegisteredAt()).isEqualTo(registeredAt);
        assertThat(findBy.getSettlementDetails()).hasSize(3)
                .extracting("receiverId", "amount")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1000),
                        tuple(3L, 2000),
                        tuple(4L, 3000)
                );

    }

    @DisplayName("주어진 정산하기 요청 Id 와 요청 대상자(receiverId)로 완료 처리한다.")
    @Test
    @Transactional
    void paySettlementComplete() {

        // given
        Long receiverId = 1L;

        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(receiverId, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail3 = createSettlementDetail(5L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail4 = createSettlementDetail(6L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(1L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));
        SettlementRequest settlementRequest2 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail3, settlementDetail4));

        List<SettlementRequest> savedAll = settlementRequestRepository.saveAll(List.of(settlementRequest1, settlementRequest2));

        Long requestId = savedAll.get(0).getRequestId();

        // when
        settlementService.paySettlementComplete(requestId, receiverId);

        // then
        SettlementRequest result = settlementRequestRepository.findById(requestId).orElseThrow(NoSuchElementException::new);
        assertThat(result.getDetailByReceiverId(receiverId).getStatus()).isEqualTo(SettlementDetailStatus.COMPLETED);

    }

    @DisplayName("주어진 정산하기 요청 Id 와 요청 대상자(receiverId)로 완료 처리하고, 전체 완료이면 전체 완료처리한다.")
    @Test
    @Transactional
    void paySettlementCompleteAndTotalComplete() {

        // given
        Long receiverId = 1L;

        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.COMPLETED);
        SettlementDetail settlementDetail2 = createSettlementDetail(receiverId, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail3 = createSettlementDetail(5L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail4 = createSettlementDetail(6L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(1L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));
        SettlementRequest settlementRequest2 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail3, settlementDetail4));

        List<SettlementRequest> savedAll = settlementRequestRepository.saveAll(List.of(settlementRequest1, settlementRequest2));

        Long requestId = savedAll.get(0).getRequestId();

        // when
        settlementService.paySettlementComplete(requestId, receiverId);

        // then
        SettlementRequest result = settlementRequestRepository.findById(requestId).orElseThrow(NoSuchElementException::new);
        assertThat(result.getDetailByReceiverId(receiverId).getStatus()).isEqualTo(SettlementDetailStatus.COMPLETED);
        assertThat(result.getStatus()).isEqualTo(SettlementRequestStatus.COMPLETED);

    }

    @DisplayName("주어진 정산하기 요청 Id 와 요청 대상자(receiverId)로 완료 처리하고, 전체 완료이면 전체 완료처리한다.")
    @Test
    void paySettlementCompleteAndTotalComplete2() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest = createSettlementRequest(1L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));
        SettlementRequest saved = settlementRequestRepository.save(settlementRequest);

        Long requestId = saved.getRequestId();

        Long detailId1 = saved.getDetailByReceiverId(3L).getDetailId();
        Long detailId2 = saved.getDetailByReceiverId(4L).getDetailId();

        // when
        settlementService.paySettlementComplete(requestId, 3L);
        settlementService.paySettlementComplete(requestId, 4L);

        // then
        SettlementDetail detailResult1 = settlementDetailRepository.findById(detailId1).orElseThrow();
        SettlementDetail detailResult2 = settlementDetailRepository.findById(detailId2).orElseThrow();

        assertThat(detailResult1.getStatus()).isEqualTo(SettlementDetailStatus.COMPLETED);
        assertThat(detailResult2.getStatus()).isEqualTo(SettlementDetailStatus.COMPLETED);

        SettlementRequest result = settlementRequestRepository.findById(requestId).orElseThrow(NoSuchElementException::new);
        assertThat(result.getStatus()).isEqualTo(SettlementRequestStatus.COMPLETED);

    }

    @DisplayName("주어진 detailId 리스트의 상태를 REMIND 로 업데이트 한다.")
    @Test
    void remindSettlement() {

        // given
        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail3 = createSettlementDetail(5L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail4 = createSettlementDetail(6L, 1000, SettlementDetailStatus.PENDING);

        List<SettlementDetail> settlementDetails = settlementDetailRepository.saveAll(List.of(settlementDetail1, settlementDetail2, settlementDetail3, settlementDetail4));

        List<Long> detailIds = settlementDetails.stream()
                .map(SettlementDetail::getDetailId)
                .toList();

        // when
        settlementService.remindSettlement(detailIds);

        // then
        List<SettlementDetail> result = settlementDetailRepository.findAllById(detailIds);
        assertThat(result).hasSize(4)
                .extracting("receiverId", "status")
                .containsExactlyInAnyOrder(
                        tuple(3L, SettlementDetailStatus.REMINDED),
                        tuple(4L, SettlementDetailStatus.REMINDED),
                        tuple(5L, SettlementDetailStatus.REMINDED),
                        tuple(6L, SettlementDetailStatus.REMINDED)
                );
    }

    private static SettlementPieceServiceRequest createSettlementPiece(Long receiverId, int amount) {
        return SettlementPieceServiceRequest.builder()
                .receiverId(receiverId)
                .amount(amount)
                .build();
    }

    private static SettlementCreateServiceRequest createSettlementCreate(Long requesterId, List<SettlementPieceServiceRequest> pieceServiceRequests) {
        return SettlementCreateServiceRequest.builder()
                .requesterId(requesterId)
                .settlementPieceServiceRequests(pieceServiceRequests)
                .build();
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