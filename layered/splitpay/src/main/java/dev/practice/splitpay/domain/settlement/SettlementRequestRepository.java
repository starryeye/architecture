package dev.practice.splitpay.domain.settlement;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettlementRequestRepository extends JpaRepository<SettlementRequest, Long> {

    @EntityGraph(attributePaths = {"settlementDetails"})
    Optional<SettlementRequest> findEntityGraphByRequestId(Long requestId);

    List<SettlementRequest> findAllByRequesterIdOrderByCreatedAtDesc(Long requesterId);
}
