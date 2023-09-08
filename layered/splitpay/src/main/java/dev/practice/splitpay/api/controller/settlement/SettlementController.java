package dev.practice.splitpay.api.controller.settlement;

import dev.practice.splitpay.api.ApiResponse;
import dev.practice.splitpay.api.controller.settlement.request.SettlementCreateRequest;
import dev.practice.splitpay.api.facade.CreateSettlementUseCase;
import dev.practice.splitpay.api.facade.PaySettlementUseCase;
import dev.practice.splitpay.api.facade.RemindSettlementUseCase;
import dev.practice.splitpay.api.facade.response.SettlementDetailResponse;
import dev.practice.splitpay.api.facade.response.SettlementRequestAndDetailsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final CreateSettlementUseCase createSettlementUseCase;
    private final PaySettlementUseCase paySettlementUseCase;
    private final RemindSettlementUseCase remindSettlementUseCase;

    @PostMapping("/new")
    public ApiResponse<SettlementRequestAndDetailsResponse> createSettlement(
            @RequestHeader("X-USER-ID") Long loginId,
            @Valid @RequestBody SettlementCreateRequest request
    ) {

        LocalDateTime registeredAt = LocalDateTime.now();

        return ApiResponse.ok(createSettlementUseCase.createSettlement(request.toServiceRequest(loginId), registeredAt));
    }

    @PostMapping("/{requestId}/pay")
    public ApiResponse<SettlementDetailResponse> paySettlement(
            @RequestHeader("X-USER-ID") Long loginId,
            @PathVariable Long requestId
    ) {
        return ApiResponse.ok(paySettlementUseCase.paySettlement(requestId, loginId));
    }

    @PostMapping("/{requestId}/remind")
    public ApiResponse<List<SettlementDetailResponse>> remindSettlement(
            @RequestHeader("X-USER-ID") Long loginId,
            @PathVariable Long requestId
    ) {
        return ApiResponse.ok(remindSettlementUseCase.remindSettlement(requestId, loginId));
    }
}
