package dev.practice.splitpay.domain.settlement;

import dev.practice.splitpay.IntegrationTestSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


class SettlementRequestRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private SettlementRequestRepository repository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("Entity Graph, 주어진 requestId 로 한방 쿼리")
    @Test
    void findEntityGraphByRequestId() {

        // given
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 1L;

        SettlementDetail settlementDetail1 = createSettlementDetail(2L, 1000);
        SettlementDetail settlementDetail2 = createSettlementDetail(3L, 2000);
        SettlementDetail settlementDetail3 = createSettlementDetail(4L, 3000);
        SettlementDetail settlementDetail4 = createSettlementDetail(5L, 1000);
        SettlementRequest settlementRequest = SettlementRequest.create(
                requesterId, registeredAt, List.of(settlementDetail1, settlementDetail2, settlementDetail3, settlementDetail4)
        );

        SettlementRequest saved = repository.save(settlementRequest);

        Long requestId = saved.getRequestId();

        
        // when
        System.out.println("==============before when==============");
        SettlementRequest result = repository.findEntityGraphByRequestId(requestId).orElseThrow();
        System.out.println("===============after when==============");

        // then
        result.getSettlementDetails().forEach(
                detail -> assertThat(persistenceUnitUtil.isLoaded(detail)).isTrue()
        );

        assertThat(result.getSettlementDetails()).hasSize(4)
                .extracting("receiverId", "amount")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1000),
                        tuple(3L, 2000),
                        tuple(4L, 3000),
                        tuple(5L, 1000)
                );
    }

    @DisplayName("주어진 requesterId 로 등록된 정산하기 요청 리스트를 생성시간 내림차순으로 조회한다.")
    @Test
    void findAllByRequesterIdOrderByCreatedAtDesc() {

        // given
        LocalDateTime registeredAt = LocalDateTime.of(2023, 8, 9, 23, 53, 0);

        Long requesterId = 99L;

        SettlementDetail settlementDetail1 = createSettlementDetail(1L, 1000);
        SettlementDetail settlementDetail2 = createSettlementDetail(2L, 1000);
        SettlementDetail settlementDetail3 = createSettlementDetail(3L, 1000);
        SettlementDetail settlementDetail4 = createSettlementDetail(4L, 1000);
        SettlementDetail settlementDetail5 = createSettlementDetail(5L, 1000);
        SettlementDetail settlementDetail6 = createSettlementDetail(6L, 1000);
        SettlementRequest settlementRequest1 = SettlementRequest.create(
                requesterId, registeredAt, List.of(settlementDetail1, settlementDetail2)
        );
        SettlementRequest settlementRequest2 = SettlementRequest.create(
                98L, registeredAt, List.of(settlementDetail3, settlementDetail4)
        );
        SettlementRequest settlementRequest3 = SettlementRequest.create(
                requesterId, registeredAt, List.of(settlementDetail5, settlementDetail6)
        );

        repository.saveAll(List.of(settlementRequest1, settlementRequest2, settlementRequest3));

        // when
        List<SettlementRequest> result = repository.findAllByRequesterIdOrderByCreatedAtDesc(requesterId);

        // then
        assertThat(result).hasSize(2)
                .extracting("requesterId", "registeredAt")
                .containsExactly(
                        tuple(requesterId, registeredAt),
                        tuple(requesterId, registeredAt)
                );
    }

    private static SettlementDetail createSettlementDetail(Long receiverId, int amount) {
        return SettlementDetail.builder()
                .receiverId(receiverId)
                .amount(amount)
                .status(SettlementDetailStatus.PENDING)
                .build();
    }
}