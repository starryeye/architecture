package dev.practice.splitpay.domain.history.kakaopay;

import dev.practice.splitpay.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
public class KakaopayMoneySendHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromId;
    private Long toId;
    private int amount;

    private Long requestId;

    @Enumerated(value = EnumType.STRING)
    private KakaopayMoneySendResult sendResult;

    @Builder
    private KakaopayMoneySendHistory(Long fromId, Long toId, int amount, Long requestId, KakaopayMoneySendResult sendResult) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
        this.requestId = requestId;
        this.sendResult = sendResult;
    }
}
