package dev.practice.splitpay.api.controller.settlement;

import dev.practice.splitpay.ControllerTestSupport;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.api.facade.response.SettlementRequestResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SettlementQueryControllerTest extends ControllerTestSupport {

    @DisplayName("1/N 정산하기 요청 리스트를 조회한다.")
    @Test
    void getSettlementRequests() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;
        List<SettlementRequestResponse> result = List.of();

        given(getSettlementRequestsUseCase.getSettlementRequests(anyLong()))
                .willReturn(result);

        // when
        // then
        mockMvc.perform(get("/api/v1/settlements/requests")
                        .header(headerName, userId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());

    }

    @DisplayName("1/N 정산하기 요청 리스트를 조회할 때 X-USER-ID 헤더는 필수이다.")
    @Test
    void getSettlementRequestsWithoutUserIdHeader() throws Exception {

        // given
        List<SettlementRequestResponse> result = List.of();

        given(getSettlementRequestsUseCase.getSettlementRequests(anyLong()))
                .willReturn(result);

        // when
        // then
        mockMvc.perform(get("/api/v1/settlements/requests")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-USER-ID' for method parameter type Long is not present"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @DisplayName("하나의 1/N 정산하기 요청 정보와 그의 세부 정보를 조회한다.")
    @Test
    void getSettlementRequestAndDetails() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;
        Long requestId = 10L;

        // when
        // then
        mockMvc.perform(get("/api/v1/settlements/requests/{requestId}", requestId)
                        .header(headerName, userId)

        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @DisplayName("하나의 1/N 정산하기 요청 정보와 그의 세부 정보를 조회할 때, X-USER-ID 헤더 값은 필수이다.")
    @Test
    void getSettlementRequestAndDetailsWithoutUserIdHeader() throws Exception {

        // given
        Long requestId = 10L;

        // when
        // then
        mockMvc.perform(get("/api/v1/settlements/requests/{requestId}", requestId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-USER-ID' for method parameter type Long is not present"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("1/N 정산하기를 요청 받은 리스트를 조회한다.")
    @Test
    void getSettlementReceives() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;

        List<SettlementDetailResponse> result = List.of();

        given(getSettlementDetailsUseCase.getSettlementDetails(anyLong()))
                .willReturn(result);

        // when
        // then
        mockMvc.perform(get("/api/v1/settlements/receives")
                        .header(headerName, userId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());

    }

    @DisplayName("1/N 정산하기를 요청 받은 리스트를 조회할 때, X-USER-ID 값은 필수이다.")
    @Test
    void getSettlementReceivesWithoutUserIdHeader() throws Exception {

        // given
        List<SettlementDetailResponse> result = List.of();

        given(getSettlementDetailsUseCase.getSettlementDetails(anyLong()))
                .willReturn(result);

        // when
        // then
        mockMvc.perform(get("/api/v1/settlements/receives")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-USER-ID' for method parameter type Long is not present"))
                .andExpect(jsonPath("$.data").isEmpty());

    }
}