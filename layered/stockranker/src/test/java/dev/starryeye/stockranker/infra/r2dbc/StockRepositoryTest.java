package dev.starryeye.stockranker.infra.r2dbc;

import dev.starryeye.stockranker.domain.Stock;
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
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = R2dbcAuditingTestConfig.class)
@DataR2dbcTest
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @AfterEach
    void tearDown() {
        r2dbcEntityTemplate.delete(Stock.class)
                .all()
                .block();
    }

    @DisplayName("views 를 기준으로 내림차순 정렬된 몇개를 조회한다.")
    @Test
    void findAllByOrderByViewsDesc() {

        // given
        Stock stock1 = Stock.create("CODE1", "Stock 1", new BigDecimal("100.00"), new BigDecimal("100.00"), 1000L, 100L);
        Stock stock2 = Stock.create("CODE2", "Stock 2", new BigDecimal("100.00"), new BigDecimal("100.00"), 1000L, 200L);
        Stock stock3 = Stock.create("CODE3", "Stock 3", new BigDecimal("100.00"), new BigDecimal("100.00"), 1000L, 300L);

        Pageable pageable = PageRequest.of(0, 2);

        Flux<Stock> saved = stockRepository.saveAll(List.of(stock1, stock2, stock3));

        // when
        Flux<Stock> result = saved.thenMany(stockRepository.findAllByOrderByViewsDesc(pageable));

        // then
        StepVerifier.create(result)
                .expectNextMatches(stock -> stock.getCode().equals("CODE3"))
                .expectNextMatches(stock -> stock.getCode().equals("CODE2"))
                .verifyComplete();
    }

    @DisplayName("priceChangeRatio 를 기준으로 내림차순 정렬된 몇개를 조회한다.")
    @Test
    void findAllByOrderByPriceChangeRatioDesc() {

        // given
        Stock stock1 = Stock.create("CODE1", "Stock 1", new BigDecimal("90.00"), new BigDecimal("100.00"), 1000L, 100L);
        Stock stock2 = Stock.create("CODE2", "Stock 2", new BigDecimal("180.00"), new BigDecimal("100.00"), 1000L, 100L);
        Stock stock3 = Stock.create("CODE3", "Stock 3", new BigDecimal("270.00"), new BigDecimal("100.00"), 1000L, 100L);

        Pageable pageable = PageRequest.of(0, 2);

        Flux<Stock> saved = stockRepository.saveAll(List.of(stock1, stock2, stock3));

        // when
        Flux<Stock> result = saved.thenMany(stockRepository.findAllByOrderByPriceChangeRatioDesc(pageable));

        // then
        StepVerifier.create(result)
                .expectNextMatches(stock -> stock.getCode().equals("CODE3"))
                .expectNextMatches(stock -> stock.getCode().equals("CODE2"))
                .verifyComplete();
    }

    @DisplayName("priceChangeRatio 를 기준으로 오름차순 정렬된 몇개를 조회한다.")
    @Test
    void findAllByOrderByPriceChangeRatioAsc() {

        // given
        Stock stock1 = Stock.create("CODE1", "Stock 1", new BigDecimal("30.00"), new BigDecimal("100.00"), 1000L, 100L);
        Stock stock2 = Stock.create("CODE2", "Stock 2", new BigDecimal("100.00"), new BigDecimal("100.00"), 1000L, 100L);
        Stock stock3 = Stock.create("CODE3", "Stock 3", new BigDecimal("300.00"), new BigDecimal("100.00"), 1000L, 100L);

        Pageable pageable = PageRequest.of(0, 2);

        Flux<Stock> saved = stockRepository.saveAll(List.of(stock1, stock2, stock3));

        // when
        Flux<Stock> result = saved.thenMany(stockRepository.findAllByOrderByPriceChangeRatioAsc(pageable));

        // then
        StepVerifier.create(result)
                .expectNextMatches(stock -> stock.getCode().equals("CODE1"))
                .expectNextMatches(stock -> stock.getCode().equals("CODE2"))
                .verifyComplete();
    }

    @DisplayName("volume 을 기준으로 내림차순 정렬된 몇개를 조회한다.")
    @Test
    void findAllByOrderByVolumeDesc() {

        // given
        Stock stock1 = Stock.create("CODE1", "Stock 1", new BigDecimal("100.00"), new BigDecimal("100.00"), 1000L, 100L);
        Stock stock2 = Stock.create("CODE2", "Stock 2", new BigDecimal("100.00"), new BigDecimal("100.00"), 2000L, 100L);
        Stock stock3 = Stock.create("CODE3", "Stock 3", new BigDecimal("100.00"), new BigDecimal("100.00"), 3000L, 100L);

        Pageable pageable = PageRequest.of(0, 2);

        Flux<Stock> saved = stockRepository.saveAll(List.of(stock1, stock2, stock3));

        // when
        Flux<Stock> result = saved.thenMany(stockRepository.findAllByOrderByVolumeDesc(pageable));

        // then
        StepVerifier.create(result)
                .expectNextMatches(stock -> stock.getCode().equals("CODE3"))
                .expectNextMatches(stock -> stock.getCode().equals("CODE2"))
                .verifyComplete();
    }

    @DisplayName("id 리스트 조회")
    @Test
    void findByIdIn() {

        // given
        Stock stock1 = Stock.create("CODE1", "Stock 1", new BigDecimal("100.00"), new BigDecimal("100.00"), 1000L, 100L);
        Stock stock2 = Stock.create("CODE2", "Stock 2", new BigDecimal("100.00"), new BigDecimal("100.00"), 2000L, 100L);
        Stock stock3 = Stock.create("CODE3", "Stock 3", new BigDecimal("100.00"), new BigDecimal("100.00"), 3000L, 100L);

        List<Stock> saved = stockRepository.saveAll(List.of(stock1, stock2, stock3)).collectList().block();
        assertThat(saved).isNotNull();
        List<Long> ids = saved.stream()
                .map(Stock::getId)
                .toList();


        // when
        Flux<Stock> result = stockRepository.findByIdIn(ids);

        // then
        StepVerifier.create(result)
                .expectNextMatches(stock -> stock.getCode().equals("CODE1"))
                .expectNextMatches(stock -> stock.getCode().equals("CODE2"))
                .expectNextMatches(stock -> stock.getCode().equals("CODE3"))
                .verifyComplete();
    }

}