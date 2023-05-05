package dev.practice.pay.account.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@Table(name = "activity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerAccountId;
    private Long sourceAccountId;
    private Long targetAccountId;
    private Long amount;

    private LocalDateTime createdAt;

    @Builder
    public ActivityJpaEntity(Long ownerAccountId, Long sourceAccountId, Long targetAccountId, Long amount, LocalDateTime createdAt) {
        this.ownerAccountId = Objects.requireNonNull(ownerAccountId);
        this.sourceAccountId = Objects.requireNonNull(sourceAccountId);
        this.targetAccountId = Objects.requireNonNull(targetAccountId);
        this.amount = Objects.requireNonNull(amount);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}
