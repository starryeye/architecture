package dev.practice.splitpay.api.controller.settlement;

import dev.practice.splitpay.api.ApiResponse;
import dev.practice.splitpay.api.facade.GetSettlementDetailsUseCase;
import dev.practice.splitpay.api.facade.GetSettlementRequestAndDetailsUseCase;
import dev.practice.splitpay.api.facade.GetSettlementRequestsUseCase;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.api.facade.response.SettlementRequestAndDetailsResponse;
import dev.practice.splitpay.api.facade.response.SettlementRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementQueryController {

    private final GetSettlementDetailsUseCase getSettlementDetailsUseCase;
    private final GetSettlementRequestsUseCase getSettlementRequestsUseCase;
    private final GetSettlementRequestAndDetailsUseCase getSettlementRequestAndDetailsUseCase;

    @GetMapping("/requests")
    public ApiResponse<List<SettlementRequestResponse>> getSettlementRequests(
            @RequestHeader("X-USER-ID") Long loginId
    ) {
        return ApiResponse.ok(getSettlementRequestsUseCase.getSettlementRequests(loginId));
    }

    @GetMapping("/requests/{requestId}")
    public ApiResponse<SettlementRequestAndDetailsResponse> getSettlementRequestAndDetails(
            @RequestHeader("X-USER-ID") Long loginId,
            @PathVariable Long requestId
    ) {
        return ApiResponse.ok(getSettlementRequestAndDetailsUseCase.getSettlementRequestAndDetails(requestId, loginId));
    }

    @GetMapping("/receives")
    public ApiResponse<List<SettlementDetailResponse>> getSettlementReceives(
            @RequestHeader("X-USER-ID") Long loginId
    ) {
        return ApiResponse.ok(getSettlementDetailsUseCase.getSettlementDetails(loginId));
    }
}
