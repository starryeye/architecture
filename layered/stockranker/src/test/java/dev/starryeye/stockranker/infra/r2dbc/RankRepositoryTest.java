package dev.starryeye.stockranker.infra.r2dbc;

import dev.starryeye.stockranker.domain.Rank;
import dev.starryeye.stockranker.testconfig.R2dbcAuditingTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ContextConfiguration(classes = R2dbcAuditingTestConfig.class)
@DataR2dbcTest
class RankRepositoryTest {

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @AfterEach
    void tearDown() {
        r2dbcEntityTemplate.delete(Rank.class)
                .all()
                .block();
    }

    @DisplayName("태그별 랭크를 오름차순으로 조회 (페이지네이션)")
    @Test
    void findByTagOrderByRankAscWithPageable() {

        // given
        Rank rank1 = Rank.create(1L, "TAG1", 1);
        Rank rank2 = Rank.create(2L, "TAG1", 2);
        Rank rank3 = Rank.create(3L, "TAG1", 3);

        List<Rank> ranks = List.of(rank1, rank2, rank3);
        rankRepository.saveAll(ranks).blockLast();

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Flux<Rank> result = rankRepository.findByTagOrderByRankAsc("TAG1", pageable);

        // then
        StepVerifier.create(result)
                .expectNextMatches(rank -> rank.getStockId().equals(1L) && rank.getRank().equals(1))
                .expectNextMatches(rank -> rank.getStockId().equals(2L) && rank.getRank().equals(2))
                .verifyComplete();
    }

    @DisplayName("태그로 랭크 삭제")
    @Test
    void deleteByTag() {

        // given
        Rank rank1 = Rank.create(1L, "TAG1", 1);
        Rank rank2 = Rank.create(2L, "TAG1", 2);

        Pageable pageable = PageRequest.of(0, 10);

        List<Rank> ranks = List.of(rank1, rank2);
        rankRepository.saveAll(ranks).blockLast();

        // when
        Mono<Void> result = rankRepository.deleteByTag("TAG1");

        // then
        StepVerifier.create(result)
                .verifyComplete();

        Flux<Rank> remainingRanks = rankRepository.findByTagOrderByRankAsc("TAG1", pageable);

        StepVerifier.create(remainingRanks)
                .verifyComplete();
    }

    @DisplayName("태그와 랭크로 조회")
    @Test
    void findByTagAndRank() {

        // given
        Rank rank1 = Rank.create(1L, "TAG1", 1);
        Rank rank2 = Rank.create(2L, "TAG1", 2);

        List<Rank> ranks = List.of(rank1, rank2);
        rankRepository.saveAll(ranks).blockLast();

        // when
        Mono<Rank> result = rankRepository.findByTagAndRank("TAG1", 1);

        // then
        StepVerifier.create(result)
                .expectNextMatches(rank -> rank.getStockId().equals(1L) && rank.getRank().equals(1))
                .verifyComplete();
    }

}