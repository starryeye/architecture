package dev.starryeye.stockranker.domain;

import dev.starryeye.stockranker.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Table("RANK")
public class Rank extends BaseEntity {

    @Id
    private final Long id;

    private final Long stockId;

    @Column("tag")
    private final String tag;
    private final Integer rank;

    @Builder
    private Rank(LocalDateTime createdAt, LocalDateTime updatedAt, Long id, Long stockId, String tag, Integer rank) {
        super(createdAt, updatedAt);
        this.id = id;
        this.stockId = stockId;
        this.tag = tag;
        this.rank = rank;
    }

    public static Rank create(Long stockId, String tag, Integer rank) {
        return Rank.builder()
                .createdAt(null)
                .updatedAt(null)
                .id(null)
                .stockId(stockId)
                .tag(tag)
                .rank(rank)
                .build();
    }

    public Rank changeStockId(Long stockId) {
        return Rank.builder()
                .createdAt(getCreatedAt())
                .updatedAt(null)
                .id(getId())
                .stockId(stockId)
                .tag(getTag())
                .rank(getRank())
                .build();
    }
}
