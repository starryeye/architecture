package dev.practice.splitpay.domain.settlement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementDetailRepository extends JpaRepository<SettlementDetail, Long> {

    List<SettlementDetail> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId);

}
