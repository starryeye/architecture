package dev.practice.splitpay.domain.settlement;

import dev.practice.splitpay.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SettlementDetailTest extends IntegrationTestSupport {

    @DisplayName("정산 요청 대상자의 정산 상세 생성시 상태는 PENDING 이다.")
    @Test
    void pending() {

        // given
        // when
        SettlementDetail settlementDetail = SettlementDetail.create(1L, 1000);

        // then
        assertThat(settlementDetail.getStatus()).isEqualTo(SettlementDetailStatus.PENDING);
    }

    @DisplayName("SettlementDetail 의 상태 값을 변경할 수 있다.")
    @Test
    void updateStatus() {

        // given
        SettlementDetail settlementDetail = SettlementDetail.create(1L, 1000);

        // when
        settlementDetail.updateStatus(SettlementDetailStatus.COMPLETED);

        // then
        assertThat(settlementDetail.getStatus()).isEqualTo(SettlementDetailStatus.COMPLETED);
    }

}