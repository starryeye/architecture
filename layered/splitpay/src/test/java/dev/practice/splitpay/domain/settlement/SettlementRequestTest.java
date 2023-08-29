package dev.practice.splitpay.domain.settlement;

import dev.practice.splitpay.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static dev.practice.splitpay.domain.settlement.SettlementDetailStatus.*;
import static org.assertj.core.api.Assertions.*;

class SettlementRequestTest extends IntegrationTestSupport {


    @DisplayName("1/N 정산하기 요청을 생성하면, 요청 상태는 PENDING 이다.")
    @Test
    void pending() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, PENDING)
        );

        // when
        SettlementRequest settlementRequest = SettlementRequest.create(requesterId, registeredAt, settlementDetails);

        // then
        assertThat(settlementRequest.getStatus()).isEqualTo(SettlementRequestStatus.PENDING);
    }

    @DisplayName("1/N 정산하기 요청을 생성하면, 요청 대상에 대한 상세 요청이 포함된다.")
    @Test
    void settlementRequestIncludeSettlementDetails() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, PENDING)
        );

        // when
        SettlementRequest settlementRequest = SettlementRequest.create(requesterId, registeredAt, settlementDetails);

        // then
        assertThat(settlementRequest.getSettlementDetails()).hasSize(2)
                .extracting("receiverId", "amount")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1000),
                        tuple(3L, 2000)
                );
    }

    @DisplayName("1/N 정산하기 요청을 생성하면, 요청 대상에 대한 상세 요청(SettlementDetail)은 settlementRequest 참조한다.")
    @Test
    void settlementDetailsIncludeSettlementRequest() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING)
        );

        // when
        SettlementRequest settlementRequest = SettlementRequest.create(requesterId, registeredAt, settlementDetails);

        // then
        assertThat(settlementRequest.getSettlementDetails().get(0).getSettlementRequest()).isEqualTo(settlementRequest);
    }

    @DisplayName("1/N 정산하기 요청을 생성하면, 정산 금액의 총액을 계산한다.")
    @Test
    void createCalculateTotalAmount() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, PENDING)
        );

        // when
        SettlementRequest settlementRequest = SettlementRequest.create(requesterId, registeredAt, settlementDetails);

        // then
        assertThat(settlementRequest.getTotalAmount()).isEqualTo(3000);
    }

    @DisplayName("1/N 정산하기 요청을 생성하면, 자기 자신의 정산 상태를 COMPLETED 로 바꾼다.")
    @Test
    void createSelfCompleteDetail() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(requesterId, 3000, PENDING),
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, PENDING)
        );

        // when
        SettlementRequest settlementRequest = SettlementRequest.create(
                requesterId,
                registeredAt,
                settlementDetails
        );

        // then
        List<SettlementDetail> result = settlementRequest.getSettlementDetails().stream()
                .filter((detail) -> detail.getStatus() == COMPLETED)
                .toList();

        assertThat(result).hasSize(1)
                .extracting("receiverId", "amount", "status")
                .containsExactly(
                        tuple(requesterId, 3000, COMPLETED)
                );
    }

    @DisplayName("1/N 정산하기 요청을 생성하면, 요청 대상자의 receiverId 는 중복 될 수 없다.")
    @Test
    void createHasDuplicationReceiverId() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(2L, 2000, PENDING)
        );

        // when
        // then
        assertThatThrownBy(
                () -> SettlementRequest.create(requesterId, registeredAt, settlementDetails)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하나의 1/N 정산하기 요청에 중복된 요청 대상자가 존재할 수 없습니다.");

    }

    @DisplayName("주어진 receiverId 들의 Detail 의 상태를 변경한다.")
    @Test
    void updateDetailStatus() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long receiverId1 = 2L;
        Long receiverId2 = 3L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(receiverId1, 1000, PENDING),
                createSettlementDetail(receiverId2, 2000, PENDING),
                createSettlementDetail(4L, 2000, PENDING)
        );

        SettlementRequest settlementRequest = SettlementRequest.builder()
                .requesterId(1L)
                .registeredAt(registeredAt)
                .status(SettlementRequestStatus.PENDING)
                .settlementDetails(settlementDetails)
                .build();

        // when
        settlementRequest.updateDetailsStatus(List.of(receiverId1, receiverId2), REMINDED);

        // then
        List<SettlementDetail> result = settlementRequest.getSettlementDetails().stream()
                .filter((detail) -> detail.getStatus() == REMINDED)
                .toList();
        assertThat(result).hasSize(2)
                .extracting("receiverId", "amount", "status")
                .containsExactlyInAnyOrder(
                        tuple(receiverId1, 1000, REMINDED),
                        tuple(receiverId2, 2000, REMINDED)
                );
    }

    @DisplayName("1/N 정산하기 요청의 상태는 하위(요청 받은 사람 모두, Detail) 상태가 모두 COMPLETED 가 되면 COMPLETED 가 된다.")
    @Test
    void checkCompleteStatus() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, COMPLETED),
                createSettlementDetail(3L, 2000, COMPLETED),
                createSettlementDetail(4L, 2000, COMPLETED)
        );

        SettlementRequest settlementRequest = SettlementRequest.builder()
                .requesterId(1L)
                .registeredAt(registeredAt)
                .status(SettlementRequestStatus.PENDING)
                .settlementDetails(settlementDetails)
                .build();

        // when
        settlementRequest.checkCompleteStatus();

        // then
        assertThat(settlementRequest.getStatus()).isEqualTo(SettlementRequestStatus.COMPLETED);
    }

    @DisplayName("1/N 정산하기 요청의 상태는 하위(요청 받은 사람 모두, Detail) 상태가 모두 COMPLETED 가 아니면 상태 변경 되지 않는다.")
    @Test
    void checkCompleteStatusWithNoAllCompleted() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, COMPLETED),
                createSettlementDetail(4L, 2000, COMPLETED)
        );

        SettlementRequest settlementRequest = SettlementRequest.builder()
                .requesterId(1L)
                .registeredAt(registeredAt)
                .status(SettlementRequestStatus.PENDING)
                .settlementDetails(settlementDetails)
                .build();

        SettlementRequestStatus beforeStatus = settlementRequest.getStatus();

        // when
        settlementRequest.checkCompleteStatus();

        // then
        assertThat(settlementRequest.getStatus()).isEqualTo(beforeStatus);
    }

    @DisplayName("정산하기 요청 상태를 변경한다.")
    @Test
    void updateStatus() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, PENDING)
        );

        SettlementRequest settlementRequest = SettlementRequest.builder()
                .requesterId(1L)
                .status(SettlementRequestStatus.PENDING)
                .registeredAt(registeredAt)
                .settlementDetails(settlementDetails)
                .build();

        // when
        settlementRequest.updateStatus(SettlementRequestStatus.COMPLETED);

        // then
        assertThat(settlementRequest.getStatus()).isEqualTo(SettlementRequestStatus.COMPLETED);
    }

    @DisplayName("Completed 상태인 Detail 을 제외한 Detail 리스트를 반환한다.")
    @Test
    void getFilteredWithoutCompletedDetails() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, COMPLETED),
                createSettlementDetail(4L, 3000, PENDING),
                createSettlementDetail(5L, 4000, COMPLETED)
        );

        SettlementRequest settlementRequest = SettlementRequest.builder()
                .requesterId(1L)
                .status(SettlementRequestStatus.PENDING)
                .registeredAt(registeredAt)
                .settlementDetails(settlementDetails)
                .build();

        // when
        List<SettlementDetail> filtered = settlementRequest.getFilteredWithoutCompletedDetails();

        // then
        assertThat(filtered).hasSize(2)
                .extracting("receiverId", "amount", "status")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1000, PENDING),
                        tuple(4L, 3000, PENDING)
                );
    }

    @DisplayName("주어진 정산하기 요청에서 주어진 요청 대상자의 상세 정보를 반환받는다.")
    @Test
    void getDetailByReceiverId() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long receiverId = 2L;

        SettlementDetail targetDetail = createSettlementDetail(receiverId, 1000, PENDING);

        List<SettlementDetail> settlementDetails = List.of(
                targetDetail,
                createSettlementDetail(3L, 2000, PENDING)
        );

        SettlementRequest settlementRequest = SettlementRequest.builder()
                .requesterId(1L)
                .status(SettlementRequestStatus.PENDING)
                .registeredAt(registeredAt)
                .settlementDetails(settlementDetails)
                .build();

        // when
        SettlementDetail result = settlementRequest.getDetailByReceiverId(receiverId);

        // then
        assertThat(result).isEqualTo(targetDetail);
    }

    @DisplayName("주어진 정산하기 요청에서 주어진 요청 대상자의 상세 정보를 찾을 수 없으면 에러 발생")
    @Test
    void getDetailByReceiverIdButNotFound() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long receiverId = 99L;

        List<SettlementDetail> settlementDetails = List.of(
                createSettlementDetail(2L, 1000, PENDING),
                createSettlementDetail(3L, 2000, PENDING)
        );

        SettlementRequest settlementRequest = SettlementRequest.builder()
                .requesterId(1L)
                .status(SettlementRequestStatus.PENDING)
                .registeredAt(registeredAt)
                .settlementDetails(settlementDetails)
                .build();

        // when
        // then
        assertThatThrownBy(
                () -> settlementRequest.getDetailByReceiverId(receiverId)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 정산에서 정산 대상자를 찾을 수 없습니다.");
    }

    private static SettlementDetail createSettlementDetail(Long receiverId, int amount, SettlementDetailStatus status) {
        return SettlementDetail.builder()
                .receiverId(receiverId)
                .amount(amount)
                .status(status)
                .build();
    }
}