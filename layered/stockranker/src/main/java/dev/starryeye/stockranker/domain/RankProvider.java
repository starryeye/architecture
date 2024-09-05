package dev.starryeye.stockranker.domain;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RankProvider {

    Flux<Rank> findByTagOrderByRankAsc(String tag, Pageable pageable);
    Mono<Void> deleteByTag(String tag);
    Mono<Rank> findByTagAndRank(String tag, int rank);
    Mono<Long> bulkUpdateRanks(List<Rank> ranks);
    Mono<Long> bulkInsertRanks(List<Rank> ranks);
}
