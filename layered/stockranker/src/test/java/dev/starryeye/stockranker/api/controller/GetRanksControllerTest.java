package dev.starryeye.stockranker.api.controller;

import dev.starryeye.stockranker.api.facade.GetRankByTagUseCase;
import dev.starryeye.stockranker.api.facade.response.RankResponse;
import dev.starryeye.stockranker.api.facade.response.StockDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = GetRanksController.class)
class GetRanksControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GetRankByTagUseCase mockUseCase;

    @DisplayName("RankController, context load test")
    @Test
    void contextLoad() {
    }

    @DisplayName("Tag와 Size로 랭킹을 가져온다.")
    @Test
    void getStocksByTag() {
        // given
        String tag = "POPULAR";
        int size = 20;
        List<StockDto> stocks = List.of(
                new StockDto(1, "STOCK1", "Stock Name 1", new BigDecimal("100.0"), new BigDecimal("90.0"), new BigDecimal("10.0"), 1000L, 500L),
                new StockDto(2, "STOCK2", "Stock Name 2", new BigDecimal("200.0"), new BigDecimal("180.0"), new BigDecimal("20.0"), 2000L, 1500L)
        );
        RankResponse response = new RankResponse(stocks, tag, stocks.size(), size + 20);

        when(mockUseCase.execute(tag, size)).thenReturn(Mono.just(response));

        // when
        EntityExchangeResult<byte[]> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ranking")
                        .queryParam("tag", tag)
                        .queryParam("size", String.valueOf(size))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tag").isEqualTo(tag)
                .jsonPath("$.totalCount").isEqualTo(stocks.size())
                .jsonPath("$.nextSize").isEqualTo(size + 20)
                .returnResult();

        // then
        ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockUseCase).execute(tagCaptor.capture(), sizeCaptor.capture());

        assertThat(tagCaptor.getValue()).isEqualTo(tag);
        assertThat(sizeCaptor.getValue()).isEqualTo(size);

        String responseBody = new String(Objects.requireNonNull(result.getResponseBody()));
        assertThat(responseBody).contains("\"tag\":\"" + tag + "\"");
        assertThat(responseBody).contains("\"totalCount\":" + stocks.size());
        assertThat(responseBody).contains("\"nextSize\":" + (size + 20));
    }

    @DisplayName("유효하지 않은 태그로 랭킹을 가져온다.")
    @Test
    void getStocksByTagWithInvalidTag() {
        // given
        String invalidTag = "";

        // when & then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ranking")
                        .queryParam("tag", invalidTag)
                        .queryParam("size", "20")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("유효하지 않은 사이즈로 랭킹을 가져온다.")
    @Test
    void getStocksByTagWithSizeIs101() {
        // given
        String tag = "POPULAR";
        int invalidSize = 101;

        // when & then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ranking")
                        .queryParam("tag", tag)
                        .queryParam("size", String.valueOf(invalidSize))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("유효하지 않은 사이즈로 랭킹을 가져온다.")
    @Test
    void getStocksByTagWithSizeIsZero() {
        // given
        String tag = "POPULAR";
        int invalidSize = 0;

        // when & then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ranking")
                        .queryParam("tag", tag)
                        .queryParam("size", String.valueOf(invalidSize))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("기본 사이즈로 랭킹을 가져온다.")
    @Test
    void getStocksByTagWithDefaultSize() {
        // given
        String tag = "POPULAR";

        List<StockDto> stocks = List.of(
                new StockDto(1, "STOCK1", "Stock Name 1", new BigDecimal("100.0"), new BigDecimal("90.0"), new BigDecimal("10.0"), 1000L, 500L),
                new StockDto(2, "STOCK2", "Stock Name 2", new BigDecimal("200.0"), new BigDecimal("180.0"), new BigDecimal("20.0"), 2000L, 1500L)
        );
        RankResponse response = new RankResponse(stocks, tag, stocks.size(), 40); // 기본 크기 + 20

        when(mockUseCase.execute(tag, 20)).thenReturn(Mono.just(response));

        // when
        EntityExchangeResult<byte[]> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ranking")
                        .queryParam("tag", tag)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tag").isEqualTo(tag)
                .jsonPath("$.totalCount").isEqualTo(stocks.size())
                .jsonPath("$.nextSize").isEqualTo(40) // 기본 크기 + 20
                .returnResult();

        // then
        ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockUseCase).execute(tagCaptor.capture(), sizeCaptor.capture());

        assertThat(tagCaptor.getValue()).isEqualTo(tag);
        assertThat(sizeCaptor.getValue()).isEqualTo(20);

        String responseBody = new String(Objects.requireNonNull(result.getResponseBody()));
        assertThat(responseBody).contains("\"tag\":\"" + tag + "\"");
        assertThat(responseBody).contains("\"totalCount\":" + stocks.size());
        assertThat(responseBody).contains("\"nextSize\":40"); // 기본 크기 + 20
    }
}
