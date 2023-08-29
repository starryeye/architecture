package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.IntegrationTestSupport;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.api.service.message.NotificationService;
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

class RemindSettlementUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private RemindSettlementUseCase remindSettlementUseCase;

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

    @DisplayName("아직 정산이 완료되지 않은 사람들에게 알림을 보내고(Mock) 상태를 업데이트한다.")
    @Test
    void remindSettlement() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 2L;

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.COMPLETED);
        SettlementDetail settlementDetail3 = createSettlementDetail(5L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(requesterId, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2, settlementDetail3));

        SettlementRequest saved = settlementRequestRepository.save(settlementRequest1);

        Long requestId = saved.getRequestId();

        Long detailId1 = saved.getDetailByReceiverId(3L).getDetailId();
        Long detailId2 = saved.getDetailByReceiverId(5L).getDetailId();

        // when
        List<SettlementDetailResponse> result = remindSettlementUseCase.remindSettlement(requestId, requesterId);

        // then
        assertThat(result).hasSize(2)
                .extracting("receiverId", "amount", "status", "requestId")
                .containsExactlyInAnyOrder(
                        tuple(3L, 1000, SettlementDetailStatus.REMINDED, requestId),
                        tuple(5L, 1000, SettlementDetailStatus.REMINDED, requestId)
                );

        SettlementDetail result1 = settlementDetailRepository.findById(detailId1).orElseThrow();
        SettlementDetail result2 = settlementDetailRepository.findById(detailId2).orElseThrow();

        assertThat(result1.getStatus()).isEqualTo(SettlementDetailStatus.REMINDED);
        assertThat(result2.getStatus()).isEqualTo(SettlementDetailStatus.REMINDED);
    }

    @DisplayName("아직 정산이 완료되지 않은 사람들에게 알림을 보내려했지만, 정산하기 등록자와 리마인드 알림 요청자의 Id가 다르면 에러 발생")
    @Test
    void remindSettlementButNotMatch() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 99L;

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.COMPLETED);
        SettlementDetail settlementDetail3 = createSettlementDetail(5L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2, settlementDetail3));

        SettlementRequest saved = settlementRequestRepository.save(settlementRequest1);

        Long requestId = saved.getRequestId();

        // when
        // then
        assertThatThrownBy(
                () -> remindSettlementUseCase.remindSettlement(requestId, requesterId)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1/N 정산하기 요청자와 로그인 사용자가 다릅니다.");
    }

    @DisplayName("아직 정산이 완료되지 않은 사람들에게 알림을 보내는데 실패하면(Mock) 상태를 업데이트하지 않고 예외 발생")
    @Test
    void remindSettlementButNotificationFail() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 2L;

        SettlementDetail settlementDetail1 = createSettlementDetail(3L, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.COMPLETED);
        SettlementDetail settlementDetail3 = createSettlementDetail(5L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(requesterId, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2, settlementDetail3));

        SettlementRequest saved = settlementRequestRepository.save(settlementRequest1);

        Long requestId = saved.getRequestId();

        Long detailId1 = saved.getDetailByReceiverId(3L).getDetailId();
        Long detailId2 = saved.getDetailByReceiverId(5L).getDetailId();

        willThrow(RuntimeException.class)
                .given(notificationService).sendNotificationBulk(anyList());

        // when
        // then
        assertThatThrownBy(
                () -> remindSettlementUseCase.remindSettlement(requestId, requesterId)
        ).isInstanceOf(RuntimeException.class);


        SettlementDetail result1 = settlementDetailRepository.findById(detailId1).orElseThrow();
        SettlementDetail result2 = settlementDetailRepository.findById(detailId2).orElseThrow();

        assertThat(result1.getStatus()).isEqualTo(SettlementDetailStatus.PENDING);
        assertThat(result2.getStatus()).isEqualTo(SettlementDetailStatus.PENDING);
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