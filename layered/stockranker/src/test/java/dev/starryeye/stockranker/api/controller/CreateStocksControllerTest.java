package dev.starryeye.stockranker.api.controller;

import dev.starryeye.stockranker.api.controller.request.CreateStockRequest;
import dev.starryeye.stockranker.api.facade.CreateStockUseCase;
import dev.starryeye.stockranker.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CreateStocksController.class)
class CreateStocksControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateStockUseCase createStockUseCase;

    @Test
    @DisplayName("유효한 요청으로 주식을 생성한다")
    void createValidStocks() {

        // given
        List<CreateStockRequest> validRequestList = List.of(
                new CreateStockRequest("STOCK1", "Stock Name 1", new BigDecimal("100.0"), new BigDecimal("90.0"), 1000L, 500L),
                new CreateStockRequest("STOCK2", "Stock Name 2", new BigDecimal("200.0"), new BigDecimal("180.0"), 2000L, 1500L)
        );

        // stubbing
        when(createStockUseCase.execute(Mockito.anyList())).thenReturn(Mono.empty());

        // when
        // then
        webTestClient.post()
                .uri("/api/stocks/new")
                .bodyValue(validRequestList)
                .exchange()
                .expectStatus().isOk();

        ArgumentCaptor<List<Stock>> captor = ArgumentCaptor.forClass(List.class);
        verify(createStockUseCase).execute(captor.capture());

        List<Stock> capturedStocks = captor.getValue();
        assertThat(capturedStocks).hasSize(validRequestList.size());

        for (int i = 0; i < validRequestList.size(); i++) {
            CreateStockRequest request = validRequestList.get(i);
            Stock stock = capturedStocks.get(i);
            assertThat(stock.getCode()).isEqualTo(request.code());
            assertThat(stock.getName()).isEqualTo(request.name());
            assertThat(stock.getPrice()).isEqualByComparingTo(request.price());
            assertThat(stock.getPreviousClosePrice()).isEqualByComparingTo(request.previousClosePrice());
            assertThat(stock.getVolume()).isEqualTo(request.volume());
            assertThat(stock.getViews()).isEqualTo(request.views());
        }
    }

    @Test
    @DisplayName("유효하지 않은 코드로 주식 생성 시 예외를 반환한다")
    void createStockWithInvalidCode() {

        // given
        CreateStockRequest invalidRequest = new CreateStockRequest("", "Stock Name 1", new BigDecimal("100.0"), new BigDecimal("90.0"), 1000L, 500L);

        // when
        // then
        webTestClient.post()
                .uri("/api/stocks/new")
                .bodyValue(List.of(invalidRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("유효하지 않은 이름으로 주식 생성 시 예외를 반환한다")
    void createStockWithInvalidName() {
        CreateStockRequest invalidRequest = new CreateStockRequest("STOCK1", "", new BigDecimal("100.0"), new BigDecimal("90.0"), 1000L, 500L);

        // when
        // then
        webTestClient.post()
                .uri("/api/stocks/new")
                .bodyValue(List.of(invalidRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("유효하지 않은 가격으로 주식 생성 시 예외를 반환한다")
    void createStockWithInvalidPrice() {

        // given
        CreateStockRequest invalidRequest = new CreateStockRequest("STOCK1", "Stock Name 1", BigDecimal.ZERO, new BigDecimal("90.0"), 1000L, 500L);

        // when
        // then
        webTestClient.post()
                .uri("/api/stocks/new")
                .bodyValue(List.of(invalidRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("유효하지 않은 이전 종가로 주식 생성 시 예외를 반환한다")
    void createStockWithInvalidPreviousClosePrice() {

        // given
        CreateStockRequest invalidRequest = new CreateStockRequest("STOCK1", "Stock Name 1", new BigDecimal("100.0"), BigDecimal.ZERO, 1000L, 500L);

        // when
        // then
        webTestClient.post()
                .uri("/api/stocks/new")
                .bodyValue(List.of(invalidRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("유효하지 않은 거래량으로 주식 생성 시 예외를 반환한다")
    void createStockWithInvalidVolume() {

        // given
        CreateStockRequest invalidRequest = new CreateStockRequest("STOCK1", "Stock Name 1", new BigDecimal("100.0"), new BigDecimal("90.0"), -1L, 500L);

        // when
        // then
        webTestClient.post()
                .uri("/api/stocks/new")
                .bodyValue(List.of(invalidRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("유효하지 않은 조회수로 주식 생성 시 예외를 반환한다")
    void createStockWithInvalidViews() {

        // given
        CreateStockRequest invalidRequest = new CreateStockRequest("STOCK1", "Stock Name 1", new BigDecimal("100.0"), new BigDecimal("90.0"), 1000L, -1L);

        // when
        // then
        webTestClient.post()
                .uri("/api/stocks/new")
                .bodyValue(List.of(invalidRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
