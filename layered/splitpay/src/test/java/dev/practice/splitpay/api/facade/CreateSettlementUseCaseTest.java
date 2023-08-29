package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.IntegrationTestSupport;
import dev.practice.splitpay.api.facade.response.SettlementRequestAndDetailsResponse;
import dev.practice.splitpay.api.service.message.NotificationService;
import dev.practice.splitpay.api.service.settlement.request.SettlementCreateServiceRequest;
import dev.practice.splitpay.api.service.settlement.request.SettlementPieceServiceRequest;
import dev.practice.splitpay.domain.settlement.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.willThrow;


class CreateSettlementUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private CreateSettlementUseCase createSettlementUseCase;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private SettlementRequestRepository settlementRequestRepository;

    @Autowired
    private SettlementDetailRepository settlementDetailRepository;

    @AfterEach
    void tearDown() {
        settlementDetailRepository.deleteAllInBatch();
        settlementRequestRepository.deleteAllInBatch();
    }

    @DisplayName("1/N 정산하기 요청을 생성하고 요청 대상자들에게 알림을 보낸다(Mock).")
    @Test
    void createSettlement() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        SettlementPieceServiceRequest settlementPiece1 = createSettlementPiece(2L, 1000);
        SettlementPieceServiceRequest settlementPiece2 = createSettlementPiece(3L, 2000);
        SettlementPieceServiceRequest settlementPiece3 = createSettlementPiece(4L, 3000);

        SettlementCreateServiceRequest request = createSettlementCreate(1L, List.of(settlementPiece1, settlementPiece2, settlementPiece3));

        // when
        SettlementRequestAndDetailsResponse result = createSettlementUseCase.createSettlement(request, registeredAt);

        // then
        assertThat(result.getRequesterId()).isEqualTo(1L);
        assertThat(result.getRegisteredAt()).isEqualTo(registeredAt);
        assertThat(result.getTotalAmount()).isEqualTo(6000);
        assertThat(result.getStatus()).isEqualTo(SettlementRequestStatus.PENDING);
        assertThat(result.getSettlementDetails()).hasSize(3)
                .extracting("receiverId", "amount", "status")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1000, SettlementDetailStatus.PENDING),
                        tuple(3L, 2000, SettlementDetailStatus.PENDING),
                        tuple(4L, 3000, SettlementDetailStatus.PENDING)
                );
    }

    @DisplayName("요청 대상자들에게 알림을 보내는데 실패하면, 에러 발생하며 1/N 정산하기 요청을 생성하지 않는다.")
    @Test
    void createSettlementButNotificationFail() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        SettlementPieceServiceRequest settlementPiece1 = createSettlementPiece(2L, 1000);
        SettlementPieceServiceRequest settlementPiece2 = createSettlementPiece(3L, 2000);
        SettlementPieceServiceRequest settlementPiece3 = createSettlementPiece(4L, 3000);

        SettlementCreateServiceRequest request = createSettlementCreate(1L, List.of(settlementPiece1, settlementPiece2, settlementPiece3));

        willThrow(RuntimeException.class)
                .given(notificationService).sendNotificationBulk(anyList());

        // when
        // then
        assertThatThrownBy(
                ()-> createSettlementUseCase.createSettlement(request, registeredAt)
        ).isInstanceOf(RuntimeException.class);

        List<SettlementRequest> all = settlementRequestRepository.findAll();
        assertThat(all).isEmpty();
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