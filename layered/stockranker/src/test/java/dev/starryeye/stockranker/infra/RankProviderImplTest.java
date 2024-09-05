package dev.starryeye.stockranker.infra;

import dev.starryeye.stockranker.domain.Rank;
import dev.starryeye.stockranker.infra.r2dbc.RankRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RankProviderImplTest {

    @Autowired
    private RankProviderImpl rankProvider;

    @Autowired
    private RankRepository rankRepository;

    @AfterEach
    void tearDown() {
        rankRepository.deleteAll().block();
    }

    @DisplayName("bulkUpdateRanks 는 기존 랭크를 업데이트한다.")
    @Test
    void bulkUpdateRanks() {

        // given
        String tag = "POPULAR";

        List<Rank> existingRanks = List.of(
                Rank.create(1L, tag, 1),
                Rank.create(2L, tag, 2)
        );

        List<Rank> saved = rankRepository.saveAll(existingRanks).collectList().block();
        assertThat(saved).isNotNull();

        List<Rank> updatedRanks = List.of(
                saved.get(0).changeStockId(3L),
                saved.get(1).changeStockId(4L)
        );

        // when
        Mono<Long> result = rankProvider.bulkUpdateRanks(updatedRanks);

        // then
        StepVerifier.create(result)
                .expectNext(2L)
                .verifyComplete();

        Flux<Rank> allRanks = rankRepository.findByTagOrderByRankAsc(tag, PageRequest.of(0, 100));
        StepVerifier.create(allRanks)
                .expectNextMatches(rank -> rank.getStockId().equals(3L) && rank.getRank() == 1)
                .expectNextMatches(rank -> rank.getStockId().equals(4L) && rank.getRank() == 2)
                .verifyComplete();
    }

    @DisplayName("bulkUpdateRanks 는 새로운 랭크를 추가한다.")
    @Test
    void bulkInsertRanks() {

        // given
        String tag = "POPULAR";

        List<Rank> existingRanks = List.of(
                Rank.create(1L, tag, 1),
                Rank.create(2L, tag, 2)
        );

        rankRepository.saveAll(existingRanks).blockLast();

        List<Rank> newRanks = List.of(
                Rank.create(3L, tag, 4),
                Rank.create(4L, tag, 3)
        );

        // when
        Mono<Long> result = rankProvider.bulkInsertRanks(newRanks);

        // then
        StepVerifier.create(result)
                .expectNext(2L)
                .verifyComplete();

        Flux<Rank> allRanks = rankRepository.findByTagOrderByRankAsc(tag, PageRequest.of(0, 100));
        StepVerifier.create(allRanks)
                .expectNextMatches(rank -> rank.getStockId().equals(1L) && rank.getRank() == 1)
                .expectNextMatches(rank -> rank.getStockId().equals(2L) && rank.getRank() == 2)
                .expectNextMatches(rank -> rank.getStockId().equals(4L) && rank.getRank() == 3)
                .expectNextMatches(rank -> rank.getStockId().equals(3L) && rank.getRank() == 4)
                .verifyComplete();
    }

}