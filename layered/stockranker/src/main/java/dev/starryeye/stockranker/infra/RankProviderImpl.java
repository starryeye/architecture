package dev.starryeye.stockranker.infra;

import dev.starryeye.stockranker.domain.Rank;
import dev.starryeye.stockranker.domain.RankProvider;
import dev.starryeye.stockranker.infra.r2dbc.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RankProviderImpl implements RankProvider {

    private final RankRepository rankRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<Rank> findByTagOrderByRankAsc(String tag, Pageable pageable) {
        return rankRepository.findByTagOrderByRankAsc(tag, pageable);
    }

    @Override
    public Mono<Void> deleteByTag(String tag) {
        return rankRepository.deleteByTag(tag);
    }

    @Override
    public Mono<Rank> findByTagAndRank(String tag, int rank) {
        return rankRepository.findByTagAndRank(tag, rank);
    }

    @Override
    public Mono<Long> bulkUpdateRanks(List<Rank> ranks) {
        if (ranks.isEmpty()) {
            return Mono.just(0L);
        }

        LocalDateTime now = LocalDateTime.now();
        return r2dbcEntityTemplate.getDatabaseClient()
                .inConnectionMany(connection -> Flux.fromIterable(ranks)
                        .flatMap(rank -> connection.createStatement(
                                        String.format("UPDATE RANK SET stock_id = %d, updated_at = '%s' WHERE tag = '%s' AND rank = %d",
                                                rank.getStockId(),
                                                now,
                                                rank.getTag(),
                                                rank.getRank()))
                                .execute()))
                .then(Mono.just((long) ranks.size()));
    }

    @Override
    public Mono<Long> bulkInsertRanks(List<Rank> ranks) {
        if (ranks.isEmpty()) {
            return Mono.just(0L);
        }

        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT INTO RANK (tag, stock_id, rank, created_at, updated_at) VALUES ";
        String values = ranks.stream()
                .map(rank -> String.format("('%s', %d, %d, '%s', '%s')",
                        rank.getTag(),
                        rank.getStockId(),
                        rank.getRank(),
                        now,
                        now))
                .collect(Collectors.joining(", "));
        sql += values;

        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .fetch()
                .rowsUpdated()
                .map(Long::valueOf);
    }
}
