package dev.starryeye.stockranker.api.facade;

import dev.starryeye.stockranker.api.facade.response.RankResponse;
import dev.starryeye.stockranker.api.facade.response.StockDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GetRankByTagUseCaseTest {

    @Autowired
    private GetRankByTagUseCase getRankByTagUseCase;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        Stock stock1 = Stock.create("STOCK1", "Stock Name 1", new BigDecimal("100.0"), new BigDecimal("90.0"), 1000L, 500L); // priceChangeRatio = 11.11
        Stock stock2 = Stock.create("STOCK2", "Stock Name 2", new BigDecimal("220.0"), new BigDecimal("200.0"), 2000L, 1500L); // priceChangeRatio = 10.00
        Stock stock3 = Stock.create("STOCK3", "Stock Name 3", new BigDecimal("330.0"), new BigDecimal("300.0"), 3000L, 2500L); // priceChangeRatio = 10.00
        Stock stock4 = Stock.create("STOCK4", "Stock Name 4", new BigDecimal("440.0"), new BigDecimal("400.0"), 4000L, 3500L); // priceChangeRatio = 10.00
        Stock stock5 = Stock.create("STOCK5", "Stock Name 5", new BigDecimal("550.0"), new BigDecimal("450.0"), 5000L, 4500L); // priceChangeRatio = 22.22

        List<Stock> savedStocks = stockRepository.saveAll(Flux.just(stock1, stock2, stock3, stock4, stock5)).collectList().block();
        assertThat(savedStocks).isNotNull();

        Rank rank1_popular = Rank.create(savedStocks.get(4).getId(), "POPULAR", 1); // views = 4500
        Rank rank2_popular = Rank.create(savedStocks.get(3).getId(), "POPULAR", 2); // views = 3500
        Rank rank3_popular = Rank.create(savedStocks.get(2).getId(), "POPULAR", 3); // views = 2500
        Rank rank4_popular = Rank.create(savedStocks.get(1).getId(), "POPULAR", 4); // views = 1500
        Rank rank5_popular = Rank.create(savedStocks.get(0).getId(), "POPULAR", 5); // views = 500

        Rank rank1_rising = Rank.create(savedStocks.get(4).getId(), "RISING", 1); // priceChangeRatio = 22.22
        Rank rank2_rising = Rank.create(savedStocks.get(0).getId(), "RISING", 2); // priceChangeRatio = 11.11
        Rank rank3_rising = Rank.create(savedStocks.get(1).getId(), "RISING", 3); // priceChangeRatio = 10.00
        Rank rank4_rising = Rank.create(savedStocks.get(2).getId(), "RISING", 4); // priceChangeRatio = 10.00
        Rank rank5_rising = Rank.create(savedStocks.get(3).getId(), "RISING", 5); // priceChangeRatio = 10.00

        Rank rank1_falling = Rank.create(savedStocks.get(1).getId(), "FALLING", 1); // priceChangeRatio = 10.00
        Rank rank2_falling = Rank.create(savedStocks.get(2).getId(), "FALLING", 2); // priceChangeRatio = 10.00
        Rank rank3_falling = Rank.create(savedStocks.get(3).getId(), "FALLING", 3); // priceChangeRatio = 10.00
        Rank rank4_falling = Rank.create(savedStocks.get(0).getId(), "FALLING", 4); // priceChangeRatio = 11.11
        Rank rank5_falling = Rank.create(savedStocks.get(4).getId(), "FALLING", 5); // priceChangeRatio = 22.22

        Rank rank1_volume = Rank.create(savedStocks.get(4).getId(), "VOLUME", 1); // volume = 5000
        Rank rank2_volume = Rank.create(savedStocks.get(3).getId(), "VOLUME", 2); // volume = 4000
        Rank rank3_volume = Rank.create(savedStocks.get(2).getId(), "VOLUME", 3); // volume = 3000
        Rank rank4_volume = Rank.create(savedStocks.get(1).getId(), "VOLUME", 4); // volume = 2000
        Rank rank5_volume = Rank.create(savedStocks.get(0).getId(), "VOLUME", 5); // volume = 1000

        rankRepository.saveAll(Flux.just(
                rank1_popular, rank2_popular, rank3_popular, rank4_popular, rank5_popular,
                rank1_rising, rank2_rising, rank3_rising, rank4_rising, rank5_rising,
                rank1_falling, rank2_falling, rank3_falling, rank4_falling, rank5_falling,
                rank1_volume, rank2_volume, rank3_volume, rank4_volume, rank5_volume
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        rankRepository.deleteAll().block();
        stockRepository.deleteAll().block();
    }

    @DisplayName("RISING 태그로 주식 랭킹을 가져온다")
    @Test
    void getStocksByRisingTag() {
        // given
        String tag = "RISING";
        int size = 1;

        // when
        Mono<RankResponse> responseMono = getRankByTagUseCase.execute(tag, size);

        // then
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.tag()).isEqualTo(tag);
                    assertThat(response.totalCount()).isEqualTo(size);
                    assertThat(response.stocks()).hasSize(size);

                    List<StockDto> stocks = response.stocks();
                    assertThat(stocks.getFirst().code()).isEqualTo("STOCK5"); // priceChangeRatio = 22.22
                })
                .verifyComplete();
    }

    @DisplayName("POPULAR 태그로 주식 랭킹을 가져온다")
    @Test
    void getStocksByPopularTag() {
        // given
        String tag = "POPULAR";
        int size = 2;

        // when
        Mono<RankResponse> result = getRankByTagUseCase.execute(tag, size);

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.tag()).isEqualTo(tag);
                    assertThat(response.totalCount()).isEqualTo(size);
                    assertThat(response.stocks()).hasSize(size);

                    List<StockDto> stocks = response.stocks();
                    assertThat(stocks.get(0).code()).isEqualTo("STOCK5"); // views = 4500
                    assertThat(stocks.get(1).code()).isEqualTo("STOCK4"); // views = 3500
                })
                .verifyComplete();
    }

    @DisplayName("FALLING 태그로 주식 랭킹을 가져온다")
    @Test
    void getStocksByFallingTag() {
        // given
        String tag = "FALLING";
        int size = 1;

        // when
        Mono<RankResponse> result = getRankByTagUseCase.execute(tag, size);

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.tag()).isEqualTo(tag);
                    assertThat(response.totalCount()).isEqualTo(size);
                    assertThat(response.stocks()).hasSize(size);

                    List<StockDto> stocks = response.stocks();
                    assertThat(stocks.getFirst().code()).isEqualTo("STOCK2"); // priceChangeRatio = 10.00
                })
                .verifyComplete();
    }

    @DisplayName("VOLUME 태그로 주식 랭킹을 가져온다")
    @Test
    void getStocksByVolumeTag() {
        // given
        String tag = "VOLUME";
        int size = 1;

        // when
        Mono<RankResponse> result = getRankByTagUseCase.execute(tag, size);

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.tag()).isEqualTo(tag);
                    assertThat(response.totalCount()).isEqualTo(size);
                    assertThat(response.stocks()).hasSize(size);

                    List<StockDto> stocks = response.stocks();
                    assertThat(stocks.getFirst().code()).isEqualTo("STOCK5"); // volume = 5000
                })
                .verifyComplete();
    }

    @DisplayName("존재하지 않는 태그로 주식 랭킹을 가져오면 빈 결과를 반환한다")
    @Test
    void getStocksByInvalidTag() {
        // given
        String tag = "INVALID";
        int size = 2;

        // when
        Mono<RankResponse> result = getRankByTagUseCase.execute(tag, size);

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.tag()).isEqualTo(tag);
                    assertThat(response.totalCount()).isZero();
                    assertThat(response.stocks()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("사이즈가 0 인 경우 예외를 반환한다")
    void getStocksByZeroSize() {
        // given
        String tag = "POPULAR";
        int size = 0;

        // when
        Mono<RankResponse> result = getRankByTagUseCase.execute(tag, size);

        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Size must be at least 1 and at most 100"))
                .verify();
    }

    @Test
    @DisplayName("사이즈가 100을 초과하는 경우 예외를 반환한다")
    void getStocksBySizeOver100() {
        // given
        String tag = "POPULAR";
        int size = 101;

        // when
        Mono<RankResponse> result = getRankByTagUseCase.execute(tag, size);

        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Size must be at least 1 and at most 100"))
                .verify();
    }
}
