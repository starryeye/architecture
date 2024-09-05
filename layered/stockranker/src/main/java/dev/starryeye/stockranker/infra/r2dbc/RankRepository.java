package dev.starryeye.stockranker.infra.r2dbc;

import dev.starryeye.stockranker.domain.Rank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RankRepository extends R2dbcRepository<Rank, Long> {

    Flux<Rank> findByTagOrderByRankAsc(String tag, Pageable pageable);

    Mono<Void> deleteByTag(String tag);

    Mono<Rank> findByTagAndRank(String tag, int rank);
}
