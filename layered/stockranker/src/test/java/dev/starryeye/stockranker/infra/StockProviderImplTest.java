package dev.starryeye.stockranker.infra;

import dev.starryeye.stockranker.domain.Stock;
import dev.starryeye.stockranker.infra.r2dbc.StockRepository;
import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockProviderImplTest {

    @Autowired
    private StockProviderImpl stockProvider;

    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        stockRepository.deleteAll().block();
    }

    @DisplayName("bulkUpdateStocks 는 기존 주식을 업데이트한다.")
    @Test
    void bulkUpdateStocks() {

        // given
        List<Stock> existingStocks = List.of(
                Stock.create("CODE1", "Stock 1", new BigDecimal("100.00"), new BigDecimal("90.00"), 1000L, 100L),
                Stock.create("CODE2", "Stock 2", new BigDecimal("200.00"), new BigDecimal("180.00"), 2000L, 200L)
        );

        List<Stock> saved = stockRepository.saveAll(existingStocks).collectList().block();
        assertThat(saved).isNotNull();

        List<Stock> newStocks = List.of(
                saved.get(0).updateStockStatus(new BigDecimal("110.00"), new BigDecimal("100.00"), 1500L, 150L),
                saved.get(1).updateStockStatus(new BigDecimal("210.00"), new BigDecimal("200.00"), 2500L, 250L)
        );

        // when
        Mono<Long> result = stockProvider.bulkUpdateStocks(newStocks);

        // then
        StepVerifier.create(result)
                .expectNext(2L)
                .verifyComplete();

        Flux<Stock> allStocks = stockRepository.findAll();
        StepVerifier.create(allStocks)
                .expectNextMatches(stock -> stock.getCode().equals("CODE1") && stock.getPrice().equals(new BigDecimal("110.00")))
                .expectNextMatches(stock -> stock.getCode().equals("CODE2") && stock.getPrice().equals(new BigDecimal("210.00")))
                .verifyComplete();
    }

    @DisplayName("bulkInsertStocks 는 새로운 주식을 저장한다.")
    @Test
    void bulkInsertStocks() {

        // given
        List<Stock> existingStocks = List.of(
                Stock.create("CODE1", "Stock 1", new BigDecimal("100.00"), new BigDecimal("90.00"), 1000L, 100L),
                Stock.create("CODE2", "Stock 2", new BigDecimal("200.00"), new BigDecimal("180.00"), 2000L, 200L)
        );

        stockRepository.saveAll(existingStocks).collectList().block();

        List<Stock> newStocks = List.of(
                Stock.create("CODE3", "Stock 3", new BigDecimal("300.00"), new BigDecimal("270.00"), 3000L, 300L),
                Stock.create("CODE4", "Stock 4", new BigDecimal("400.00"), new BigDecimal("360.00"), 4000L, 400L)
        );

        // when
        Mono<Long> result = stockProvider.bulkInsertStocks(newStocks);

        // then
        StepVerifier.create(result)
                .expectNext(2L)
                .verifyComplete();

        Flux<Stock> allStocks = stockRepository.findAll();
        StepVerifier.create(allStocks)
                .expectNextMatches(stock -> stock.getCode().equals("CODE1") && stock.getPrice().equals(new BigDecimal("100.00")))
                .expectNextMatches(stock -> stock.getCode().equals("CODE2") && stock.getPrice().equals(new BigDecimal("200.00")))
                .expectNextMatches(stock -> stock.getCode().equals("CODE3") && stock.getPrice().equals(new BigDecimal("300.00")))
                .expectNextMatches(stock -> stock.getCode().equals("CODE4") && stock.getPrice().equals(new BigDecimal("400.00")))
                .verifyComplete();
    }

}