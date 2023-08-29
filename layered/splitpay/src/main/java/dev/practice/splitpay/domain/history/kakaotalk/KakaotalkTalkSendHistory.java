package dev.practice.splitpay.domain.history.kakaotalk;

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
public class KakaotalkTalkSendHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromId; //User FK
    private Long toId; //User FK

    private Long requestId; //SettlementRequest FK
    private int amount;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private KakaotalkTalkSendResult sendResult;

    @Builder
    private KakaotalkTalkSendHistory(Long fromId, Long toId, Long requestId, int amount, String content, KakaotalkTalkSendResult sendResult) {
        this.fromId = fromId;
        this.toId = toId;
        this.requestId = requestId;
        this.amount = amount;
        this.content = content;
        this.sendResult = sendResult;
    }
}
