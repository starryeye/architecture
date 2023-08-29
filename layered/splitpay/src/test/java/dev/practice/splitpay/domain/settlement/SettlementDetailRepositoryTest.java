package dev.practice.splitpay.domain.settlement;

import dev.practice.splitpay.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static dev.practice.splitpay.domain.settlement.SettlementDetailStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class SettlementDetailRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private SettlementDetailRepository repository;

    @DisplayName("주어진 receiverId 로 요청 받은 정산하기 전체 리스트를 생성시간 내림차순으로 조회한다.")
    @Test
    void findByReceiverIdOrderByCreatedAtDesc() {

        // given
        SettlementDetail settlementDetail1 = createSettlementDetail(1L, 1000);
        SettlementDetail settlementDetail2 = createSettlementDetail(1L, 3000);
        SettlementDetail settlementDetail3 = createSettlementDetail(2L, 5000);
        SettlementDetail settlementDetail4 = createSettlementDetail(3L, 10000);
        SettlementDetail settlementDetail5 = createSettlementDetail(1L, 15000);
        repository.saveAll(List.of(settlementDetail1, settlementDetail2, settlementDetail3, settlementDetail4, settlementDetail5));

        // when
        List<SettlementDetail> result = repository.findAllByReceiverIdOrderByCreatedAtDesc(1L);

        // then
        assertThat(result).hasSize(3)
                .extracting("receiverId", "amount", "status")
                .containsExactly(
                        tuple(1L, 15000, PENDING),
                        tuple(1L, 3000, PENDING),
                        tuple(1L, 1000, PENDING)
                );
    }

    private static SettlementDetail createSettlementDetail(Long receiverId, int amount) {
        return SettlementDetail.builder()
                .receiverId(receiverId)
                .amount(amount)
                .status(PENDING)
                .build();
    }
}