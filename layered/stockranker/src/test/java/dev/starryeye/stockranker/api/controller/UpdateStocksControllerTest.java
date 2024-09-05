package dev.starryeye.stockranker.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.starryeye.stockranker.api.controller.request.UpdateStockRequest;
import dev.starryeye.stockranker.api.facade.UpdateStockUseCase;
import dev.starryeye.stockranker.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = UpdateStocksController.class)
class UpdateStocksControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UpdateStockUseCase mockUseCase;

    @DisplayName("StockController, context load test")
    @Test
    void contextLoad() {
    }

    @DisplayName("Stocks 을 업데이트한다.")
    @Test
    void updateStocks() throws JsonProcessingException {
        // given
        UpdateStockRequest request1 = new UpdateStockRequest(1L, BigDecimal.valueOf(100), BigDecimal.valueOf(90), 1000L, 500L);
        UpdateStockRequest request2 = new UpdateStockRequest(2L, BigDecimal.valueOf(200), BigDecimal.valueOf(180), 2000L, 1500L);
        List<UpdateStockRequest> requestList = List.of(request1, request2);

        List<Stock> expectedStocks = requestList.stream()
                .map(req -> Stock.builder()
                        .id(req.id())
                        .price(req.price())
                        .previousClosePrice(req.previousClosePrice())
                        .volume(req.volume())
                        .views(req.views())
                        .build())
                .toList();

        when(mockUseCase.execute(expectedStocks)).thenReturn(Mono.empty());

        // when
        webTestClient.put()
                .uri("/api/stocks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestList))
                .exchange()
                .expectStatus().isOk();

        // then
        ArgumentCaptor<List<Stock>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockUseCase).execute(captor.capture());

        List<Stock> capturedStocks = captor.getValue();
        assertThat(capturedStocks).hasSize(2);
        compareStocks(capturedStocks.get(0), expectedStocks.get(0));
        compareStocks(capturedStocks.get(1), expectedStocks.get(1));
    }

    private void compareStocks(Stock actual, Stock expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
        assertThat(actual.getPreviousClosePrice()).isEqualTo(expected.getPreviousClosePrice());
        assertThat(actual.getVolume()).isEqualTo(expected.getVolume());
        assertThat(actual.getViews()).isEqualTo(expected.getViews());
    }

    @DisplayName("Id 는 필수 값이다.")
    @Test
    void updateStocksWithIdIsNull() throws JsonProcessingException {
        // given
        UpdateStockRequest invalidRequest = new UpdateStockRequest(null, BigDecimal.valueOf(100), BigDecimal.valueOf(90), 1000L, 500L);
        List<UpdateStockRequest> requestList = List.of(invalidRequest);

        // when & then
        webTestClient.put()
                .uri("/api/stocks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestList))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("유효하지 않은 Price 로 Stocks 를 업데이트한다.")
    @Test
    void updateStocksWithInvalidPrice() throws JsonProcessingException {
        // given
        UpdateStockRequest invalidRequest = new UpdateStockRequest(1L, BigDecimal.valueOf(-100), BigDecimal.valueOf(90), 1000L, 500L);
        List<UpdateStockRequest> requestList = List.of(invalidRequest);

        // when & then
        webTestClient.put()
                .uri("/api/stocks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestList))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("유효하지 않은 PreviousClosePrice 로 Stocks 를 업데이트한다.")
    @Test
    void updateStocksWithInvalidPreviousClosePrice() throws JsonProcessingException {
        // given
        UpdateStockRequest invalidRequest = new UpdateStockRequest(1L, BigDecimal.valueOf(100), BigDecimal.valueOf(-90), 1000L, 500L);
        List<UpdateStockRequest> requestList = List.of(invalidRequest);

        // when & then
        webTestClient.put()
                .uri("/api/stocks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestList))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("유효하지 않은 Volume으로 Stocks를 업데이트한다.")
    @Test
    void updateStocksWithInvalidVolume() throws JsonProcessingException {
        // given
        UpdateStockRequest invalidRequest = new UpdateStockRequest(1L, BigDecimal.valueOf(100), BigDecimal.valueOf(90), -1000L, 500L);
        List<UpdateStockRequest> requestList = List.of(invalidRequest);

        // when & then
        webTestClient.put()
                .uri("/api/stocks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestList))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("유효하지 않은 Views로 Stocks를 업데이트한다.")
    @Test
    void updateStocksWithInvalidViews() throws JsonProcessingException {
        // given
        UpdateStockRequest invalidRequest = new UpdateStockRequest(1L, BigDecimal.valueOf(100), BigDecimal.valueOf(90), 1000L, -500L);
        List<UpdateStockRequest> requestList = List.of(invalidRequest);

        // when & then
        webTestClient.put()
                .uri("/api/stocks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestList))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("빈 목록으로 Stocks를 업데이트한다.")
    @Test
    void updateStocksWithEmptyList() throws JsonProcessingException {
        // given
        List<UpdateStockRequest> emptyRequestList = List.of();

        // when & then
        webTestClient.put()
                .uri("/api/stocks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(emptyRequestList))
                .exchange()
                .expectStatus().isOk();
    }
}
