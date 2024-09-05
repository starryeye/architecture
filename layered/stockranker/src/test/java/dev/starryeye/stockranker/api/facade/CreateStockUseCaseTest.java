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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreateStockUseCaseTest {

    @Autowired
    private CreateStockUseCase createStockUseCase;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {

        Stock stock1 = Stock.create("STOCK1", "Exist Stock 1", new BigDecimal("100.0"), new BigDecimal("90.0"), 1000L, 500L); // priceChangeRatio = 11.11
        Stock stock2 = Stock.create("STOCK2", "Exist Stock 2", new BigDecimal("220.0"), new BigDecimal("200.0"), 2000L, 1500L); // priceChangeRatio = 10.00

        List<Stock> savedStocks = stockRepository.saveAll(Flux.just(stock1, stock2)).collectList().block();
        assertThat(savedStocks).isNotNull();

        // 각 태그에 대해 적절한 기준으로 Rank 설정
        Rank rank1_popular = Rank.create(savedStocks.get(1).getId(), "POPULAR", 1); // views = 1500
        Rank rank2_popular = Rank.create(savedStocks.get(0).getId(), "POPULAR", 2); // views = 500

        Rank rank1_rising = Rank.create(savedStocks.get(0).getId(), "RISING", 1); // priceChangeRatio = 11.11
        Rank rank2_rising = Rank.create(savedStocks.get(1).getId(), "RISING", 2); // priceChangeRatio = 10.00

        Rank rank1_falling = Rank.create(savedStocks.get(1).getId(), "FALLING", 1); // priceChangeRatio = 10.00
        Rank rank2_falling = Rank.create(savedStocks.get(0).getId(), "FALLING", 2); // priceChangeRatio = 11.11

        Rank rank1_volume = Rank.create(savedStocks.get(1).getId(), "VOLUME", 1); // volume = 2000
        Rank rank2_volume = Rank.create(savedStocks.get(0).getId(), "VOLUME", 2); // volume = 1000

        rankRepository.saveAll(Flux.just(
                rank1_popular, rank2_popular,
                rank1_rising, rank2_rising,
                rank1_falling, rank2_falling,
                rank1_volume, rank2_volume
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        rankRepository.deleteAll().block();
        stockRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Stocks 을 추가 시킨다.")
    void updateStocksAndRanksSuccessfully() {

        // given
        Stock addStock = Stock.builder()
                .id(null)
                .code("STOCK3")
                .name("Add Stock")
                .price(new BigDecimal("150.0"))
                .previousClosePrice(new BigDecimal("140.0"))
                .volume(3000L)
                .views(1700L)
                .build();

        List<Stock> givenStocks = List.of(addStock);

        // when
        createStockUseCase.execute(givenStocks).block();

        // then
        StepVerifier.create(stockRepository.findAll())
                .assertNext(stock -> {
                    assertThat(stock.getCode()).isEqualTo("STOCK1");
                    assertThat(stock.getName()).isEqualTo("Exist Stock 1");
                    assertThat(stock.getPrice()).isEqualByComparingTo(new BigDecimal("100.0"));
                    assertThat(stock.getPreviousClosePrice()).isEqualByComparingTo(new BigDecimal("90.0"));
                    assertThat(stock.getVolume()).isEqualTo(1000L);
                    assertThat(stock.getViews()).isEqualTo(500L);
                })
                .assertNext(stock -> {
                    assertThat(stock.getCode()).isEqualTo("STOCK2");
                    assertThat(stock.getName()).isEqualTo("Exist Stock 2");
                    assertThat(stock.getPrice()).isEqualByComparingTo(new BigDecimal("220.0"));
                    assertThat(stock.getPreviousClosePrice()).isEqualByComparingTo(new BigDecimal("200.0"));
                    assertThat(stock.getVolume()).isEqualTo(2000L);
                    assertThat(stock.getViews()).isEqualTo(1500L);
                })
                .assertNext(stock -> {
                    assertThat(stock.getCode()).isEqualTo("STOCK3");
                    assertThat(stock.getName()).isEqualTo("Add Stock");
                    assertThat(stock.getPrice()).isEqualByComparingTo(new BigDecimal("150.0"));
                    assertThat(stock.getPreviousClosePrice()).isEqualByComparingTo(new BigDecimal("140.0"));
                    assertThat(stock.getVolume()).isEqualTo(3000L);
                    assertThat(stock.getViews()).isEqualTo(1700L);
                })
                .verifyComplete();
    }

}