package dev.starryeye.stockranker.api.facade;

import dev.starryeye.stockranker.domain.Rank;
import dev.starryeye.stockranker.domain.Stock;
import dev.starryeye.stockranker.infra.r2dbc.RankRepository;
import dev.starryeye.stockranker.infra.r2dbc.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UpdateRankUseCaseTest {

    @Autowired
    private UpdateRankUseCase updateRankUseCase;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private RankRepository rankRepository;

    @BeforeEach
    void setUp() {
        List<Stock> stocks = List.of(
                Stock.create("AAPL", "Apple Inc.", BigDecimal.valueOf(150), BigDecimal.valueOf(145), 1000L, 2000L), // priceChangeRatio = 3.45
                Stock.create("GOOGL", "Alphabet Inc.", BigDecimal.valueOf(2800), BigDecimal.valueOf(2700), 500L, 2500L), // priceChangeRatio = 3.70
                Stock.create("AMZN", "Amazon Inc.", BigDecimal.valueOf(3500), BigDecimal.valueOf(3400), 2000L, 1500L) // priceChangeRatio = 2.94
        );

        stockRepository.saveAll(stocks).collectList().block();

        Stock savedAppleStock = stockRepository.findByCode("AAPL").block();
        assertThat(savedAppleStock).isNotNull();
        Long appleId = savedAppleStock.getId();
        List<Rank> ranks = List.of(
                Rank.create(appleId, "POPULAR", 1),
                Rank.create(appleId, "RISING", 1),
                Rank.create(appleId, "FALLING", 1),
                Rank.create(appleId, "VOLUME", 1)
        );

        rankRepository.saveAll(ranks).collectList().block();
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteAll().block();
        rankRepository.deleteAll().block();
    }

    @DisplayName("모든 태그에 대한 랭킹을 업데이트한다.")
    @Test
    void executeAllTag() throws InterruptedException {

        // given
        // when
        CountDownLatch latch = new CountDownLatch(1);
        Mono<Void> result = updateRankUseCase.execute("ALL");

        result.doOnTerminate(latch::countDown).subscribe();

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Test timed out while waiting for async operation to complete.");
        }

        // then
        verifyRank("POPULAR", List.of("GOOGL", "AAPL", "AMZN"));
        verifyRank("RISING", List.of("GOOGL", "AAPL", "AMZN"));
        verifyRank("FALLING", List.of("AMZN", "AAPL", "GOOGL"));
        verifyRank("VOLUME", List.of("AMZN", "AAPL", "GOOGL"));
    }

    @DisplayName("인기 태그에 대한 랭킹을 업데이트한다.")
    @Test
    void executePopularTag() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Mono<Void> result = updateRankUseCase.execute("POPULAR");

        result.doOnTerminate(latch::countDown).subscribe();

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Test timed out while waiting for async operation to complete.");
        }

        verifyRank("POPULAR", List.of("GOOGL", "AAPL", "AMZN"));
    }

    @DisplayName("상승 태그에 대한 랭킹을 업데이트한다.")
    @Test
    void executeRisingTag() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Mono<Void> result = updateRankUseCase.execute("RISING");

        result.doOnTerminate(latch::countDown).subscribe();

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Test timed out while waiting for async operation to complete.");
        }

        verifyRank("RISING", List.of("GOOGL", "AAPL", "AMZN"));
    }

    @DisplayName("하락 태그에 대한 랭킹을 업데이트한다.")
    @Test
    void executeFallingTag() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Mono<Void> result = updateRankUseCase.execute("FALLING");

        result.doOnTerminate(latch::countDown).subscribe();

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Test timed out while waiting for async operation to complete.");
        }

        verifyRank("FALLING", List.of("AMZN", "AAPL", "GOOGL"));
    }

    @DisplayName("거래량 태그에 대한 랭킹을 업데이트한다.")
    @Test
    void executeVolumeTag() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Mono<Void> result = updateRankUseCase.execute("VOLUME");

        result.doOnTerminate(latch::countDown).subscribe();

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Test timed out while waiting for async operation to complete.");
        }

        verifyRank("VOLUME", List.of("AMZN", "AAPL", "GOOGL"));
    }

    @DisplayName("유효하지 않은 태그는 에러 발생")
    @Test
    void executeInvalidTag() {
        Mono<Void> result = updateRankUseCase.execute("INVALID_TAG");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    private void verifyRank(String tag, List<String> expectedOrder) {

        List<Rank> ranks = rankRepository.findByTagOrderByRankAsc(tag, PageRequest.of(0, 100)).collectList().block();
        assertThat(ranks).isNotNull();

        assertEquals(3, ranks.size());
        for (int i = 0; i < ranks.size(); i++) {
            assertEquals(expectedOrder.get(i), getStockCodeById(ranks.get(i).getStockId()));
        }
    }

    private String getStockCodeById(Long stockId) {
        Stock stock = stockRepository.findById(stockId).block();
        assertThat(stock).isNotNull();
        return stock.getCode();
    }
}
