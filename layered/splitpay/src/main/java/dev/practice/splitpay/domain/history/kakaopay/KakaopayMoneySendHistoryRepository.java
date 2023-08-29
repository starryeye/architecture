package dev.practice.splitpay.domain.history.kakaopay;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaopayMoneySendHistoryRepository extends JpaRepository<KakaopayMoneySendHistory, Long> {
}
