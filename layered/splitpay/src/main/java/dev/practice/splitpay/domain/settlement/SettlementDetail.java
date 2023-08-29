package dev.practice.splitpay.domain.settlement;

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
public class SettlementDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    private Long receiverId; // User FK

    private int amount;

    @Enumerated(value = EnumType.STRING)
    private SettlementDetailStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private SettlementRequest settlementRequest;

    @Builder
    private SettlementDetail(Long receiverId, int amount, SettlementDetailStatus status, SettlementRequest settlementRequest) {
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.settlementRequest = settlementRequest;
    }

    protected void setSettlementRequest(SettlementRequest settlementRequest) {
        this.settlementRequest = settlementRequest;
    }

    public static SettlementDetail create(Long receiverId, int amount) {
        return SettlementDetail.builder()
                .receiverId(receiverId)
                .amount(amount)
                .status(SettlementDetailStatus.PENDING)
                .build();
    }

    public void updateStatus(SettlementDetailStatus status) {
        this.status = status;
    }
}
