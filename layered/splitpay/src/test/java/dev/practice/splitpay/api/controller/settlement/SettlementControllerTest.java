package dev.practice.splitpay.api.controller.settlement;

import dev.practice.splitpay.ControllerTestSupport;
import dev.practice.splitpay.api.controller.settlement.request.SettlementCreateRequest;
import dev.practice.splitpay.api.controller.settlement.request.SettlementPieceRequest;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SettlementControllerTest extends ControllerTestSupport {

    @DisplayName("1/N 정산하기 요청을 등록한다.")
    @Test
    void createSettlement() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;

        SettlementPieceRequest pieceRequest1 = SettlementPieceRequest.builder()
                .receiverId(2L)
                .amount(1000)
                .build();
        SettlementPieceRequest pieceRequest2 = SettlementPieceRequest.builder()
                .receiverId(3L)
                .amount(2000)
                .build();

        SettlementCreateRequest request = SettlementCreateRequest.builder()
                .settlementPieceRequests(List.of(pieceRequest1, pieceRequest2))
                .build();

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/new")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @DisplayName("1/N 정산하기 요청을 등록할 때, X-USER-ID 헤더 값은 필수이다.")
    @Test
    void createSettlementWithoutUserIdHeader() throws Exception {

        // given

        SettlementPieceRequest pieceRequest1 = SettlementPieceRequest.builder()
                .receiverId(2L)
                .amount(1000)
                .build();
        SettlementPieceRequest pieceRequest2 = SettlementPieceRequest.builder()
                .receiverId(3L)
                .amount(2000)
                .build();

        SettlementCreateRequest request = SettlementCreateRequest.builder()
                .settlementPieceRequests(List.of(pieceRequest1, pieceRequest2))
                .build();

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-USER-ID' for method parameter type Long is not present"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

//    @DisplayName("1/N 정산하기 요청을 등록할 때, 요청 대상은 필수이다.")
//    @Test
//    void createSettlementWithEmptyPieceRequest() throws Exception {
//
//        // given
//        String headerName = "X-USER-ID";
//        Long userId = 1L;
//
//        SettlementCreateRequest request = SettlementCreateRequest.builder()
//                .settlementPieceRequests(List.of())
//                .build();
//
//        // when
//        // then
//        mockMvc.perform(post("/api/v1/settlements/new")
//                        .header(headerName, userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                )
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value(400))
//                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
//                .andExpect(jsonPath("$.message").value("1/N 정산하기 요청 대상은 필수입니다."))
//                .andExpect(jsonPath("$.data").isEmpty());
//    }

    @DisplayName("1/N 정산하기 요청을 등록할 때, 요청 대상은 2명 이상이어야 한다.")
    @Test
    void createSettlementWithPieceRequestSizeOne() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;

        SettlementPieceRequest pieceRequest1 = SettlementPieceRequest.builder()
                .receiverId(2L)
                .amount(1000)
                .build();

        SettlementCreateRequest request = SettlementCreateRequest.builder()
                .settlementPieceRequests(List.of(pieceRequest1))
                .build();

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/new")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("1/N 정산하기 요청 대상은 2명 이상이어야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("1/N 정산하기 요청을 등록할 때, 요청 대상의 Id 는 필수이다.")
    @Test
    void createSettlementWithoutReceiverId() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;

        SettlementPieceRequest pieceRequest1 = SettlementPieceRequest.builder()
                .amount(1000)
                .build();
        SettlementPieceRequest pieceRequest2 = SettlementPieceRequest.builder()
                .amount(1000)
                .receiverId(2L)
                .build();

        SettlementCreateRequest request = SettlementCreateRequest.builder()
                .settlementPieceRequests(List.of(pieceRequest1, pieceRequest2))
                .build();

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/new")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("1/N 정산하기 요청 대상 ID는 필수 입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("1/N 정산하기 요청을 등록할 때, 요청 대상 정산 금액은 0보다 커야한다.")
    @Test
    void createSettlementWithAmountZero() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;

        SettlementPieceRequest pieceRequest1 = SettlementPieceRequest.builder()
                .receiverId(2L)
                .amount(0)
                .build();
        SettlementPieceRequest pieceRequest2 = SettlementPieceRequest.builder()
                .receiverId(2L)
                .amount(2000)
                .build();

        SettlementCreateRequest request = SettlementCreateRequest.builder()
                .settlementPieceRequests(List.of(pieceRequest1, pieceRequest2))
                .build();

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/new")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("1/N 정산하기 요청 금액은 양수여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("요청 받은 1/N 정산하기에 대해 요청자에게 금액을 송금한다.")
    @Test
    void paySettlement() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;
        Long requestId = 10L;

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/{requestId}/pay", requestId)
                .header(headerName, userId)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @DisplayName("요청 받은 1/N 정산하기에 대해 요청자에게 금액을 송금할 때, X-USER-ID 헤더 값은 필수이다.")
    @Test
    void paySettlementWithoutUserIdHeader() throws Exception {

        // given
        Long requestId = 10L;

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/{requestId}/pay", requestId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-USER-ID' for method parameter type Long is not present"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("1/N 정산하기 요청 대상자 중 정산 금액을 송금하지 않은 대상자에게 리마인드 알림을 보낸다.")
    @Test
    void remindSettlement() throws Exception {

        // given
        String headerName = "X-USER-ID";
        Long userId = 1L;
        Long requestId = 10L;

        List<SettlementDetailResponse> result = List.of();

        given(remindSettlementUseCase.remindSettlement(anyLong(), anyLong()))
                .willReturn(result);

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/{requestId}/remind", requestId)
                .header(headerName, userId)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @DisplayName("1/N 정산하기 요청 대상자 중 정산 금액을 송금하지 않은 대상자에게 리마인드 알림을 보낼때 X-USER-ID 헤더는 필수이다.")
    @Test
    void remindSettlementWithoutUserIdHeader() throws Exception {

        // given
        Long requestId = 10L;

        // when
        // then
        mockMvc.perform(post("/api/v1/settlements/{requestId}/remind", requestId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-USER-ID' for method parameter type Long is not present"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}