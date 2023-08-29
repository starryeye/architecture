package dev.practice.splitpay.api.facade;

import dev.practice.splitpay.IntegrationTestSupport;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.api.service.payment.PaymentService;
import dev.practice.splitpay.domain.settlement.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;

class PaySettlementUseCaseTest extends IntegrationTestSupport {

    @Autowired
    private PaySettlementUseCase paySettlementUseCase;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private SettlementRequestRepository settlementRequestRepository;

    @Autowired
    private SettlementDetailRepository settlementDetailRepository;

    @AfterEach
    void tearDown() {
        settlementDetailRepository.deleteAllInBatch();
        settlementRequestRepository.deleteAllInBatch();
    }

    @DisplayName("1/N 정산을 처리(돈 송금, Mock) 하고 처리 상태를 업데이트 한다.")
    @Test
    void paySettlement() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long receiverId = 3L;

        SettlementDetail settlementDetail1 = createSettlementDetail(receiverId, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));

        SettlementRequest saved = settlementRequestRepository.save(settlementRequest1);

        Long requestId = saved.getRequestId();

        // when
        SettlementDetailResponse result = paySettlementUseCase.paySettlement(requestId, receiverId);

        // then
        assertThat(result.getReceiverId()).isEqualTo(receiverId);
        assertThat(result.getStatus()).isEqualTo(SettlementDetailStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(1000);
        assertThat(result.getRequestId()).isEqualTo(requestId);
    }

    @DisplayName("1/N 정산을 처리(돈 송금, Mock)가 실패하면 하고 처리 상태를 업데이트 하지 않는다.")
    @Test
    void paySettlementButMoneySendFail() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long receiverId = 3L;

        SettlementDetail settlementDetail1 = createSettlementDetail(receiverId, 1000, SettlementDetailStatus.PENDING);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));

        SettlementRequest saved = settlementRequestRepository.save(settlementRequest1);

        Long requestId = saved.getRequestId();

        Long detailId = saved.getDetailByReceiverId(receiverId).getDetailId();

        willThrow(RuntimeException.class)
                .given(paymentService).sendMoney(any());

        // when
        // then
        assertThatThrownBy(
                () -> paySettlementUseCase.paySettlement(requestId, receiverId)
        ).isInstanceOf(RuntimeException.class);

        SettlementDetail detail = settlementDetailRepository.findById(detailId).orElseThrow();
        assertThat(detail.getStatus()).isEqualTo(SettlementDetailStatus.PENDING);
    }

    @DisplayName("1/N 정산 처리 상태가 이미 완료이면 예외 발생")
    @Test
    void paySettlementButAlreadyComplete() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long receiverId = 3L;

        SettlementDetail settlementDetail1 = createSettlementDetail(receiverId, 1000, SettlementDetailStatus.COMPLETED);
        SettlementDetail settlementDetail2 = createSettlementDetail(4L, 1000, SettlementDetailStatus.PENDING);

        SettlementRequest settlementRequest1 = createSettlementRequest(2L, SettlementRequestStatus.PENDING, registeredAt, List.of(settlementDetail1, settlementDetail2));

        SettlementRequest saved = settlementRequestRepository.save(settlementRequest1);

        Long requestId = saved.getRequestId();


        // when
        // then
        assertThatThrownBy(
                () -> paySettlementUseCase.paySettlement(requestId, receiverId)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 송금 완료하였습니다.");
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