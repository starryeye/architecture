package dev.starryeye.stockranker.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.starryeye.stockranker.api.controller.request.UpdateRankRequest;
import dev.starryeye.stockranker.api.facade.UpdateRankUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = UpdateRanksController.class)
class UpdateRanksControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UpdateRankUseCase updateRankUseCase;

    @Test
    @DisplayName("모든 랭크를 업데이트한다")
    void updateAllRanks() throws JsonProcessingException {

        // given
        UpdateRankRequest request = new UpdateRankRequest("ALL");

        // stubbing
        Mockito.when(updateRankUseCase.execute(Mockito.anyString())).thenReturn(Mono.empty());

        // when
        // then
        webTestClient.post()
                .uri("/api/ranking/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("POPULAR 태그에 대해 랭크를 업데이트한다")
    void updatePopularRank() throws JsonProcessingException {

        // given
        UpdateRankRequest request = new UpdateRankRequest("POPULAR");

        // stubbing
        Mockito.when(updateRankUseCase.execute(Mockito.anyString())).thenReturn(Mono.empty());

        // when
        // then
        webTestClient.post()
                .uri("/api/ranking/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("RISING 태그에 대해 랭크를 업데이트한다")
    void updateRisingRank() throws JsonProcessingException {

        // given
        UpdateRankRequest request = new UpdateRankRequest("RISING");

        // stubbing
        Mockito.when(updateRankUseCase.execute(Mockito.anyString())).thenReturn(Mono.empty());

        // when
        // then
        webTestClient.post()
                .uri("/api/ranking/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("FALLING 태그에 대해 랭크를 업데이트한다")
    void updateFallingRank() throws JsonProcessingException {

        // given
        UpdateRankRequest request = new UpdateRankRequest("FALLING");

        // stubbing
        Mockito.when(updateRankUseCase.execute(Mockito.anyString())).thenReturn(Mono.empty());

        // when
        // then
        webTestClient.post()
                .uri("/api/ranking/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("VOLUME 태그에 대해 랭크를 업데이트한다")
    void updateVolumeRank() throws JsonProcessingException {

        // given
        UpdateRankRequest request = new UpdateRankRequest("VOLUME");

        // stubbing
        Mockito.when(updateRankUseCase.execute(Mockito.anyString())).thenReturn(Mono.empty());

        // when
        // then
        webTestClient.post()
                .uri("/api/ranking/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("잘못된 태그로 랭크 업데이트 시 400 에러를 반환한다")
    void updateInvalidTagRank() throws JsonProcessingException {

        // given
        UpdateRankRequest request = new UpdateRankRequest("INVALID");

        // stubbing
        Mockito.when(updateRankUseCase.execute(Mockito.anyString())).thenReturn(Mono.empty());

        // when
        // then
        webTestClient.post()
                .uri("/api/ranking/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
