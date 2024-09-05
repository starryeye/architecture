package dev.starryeye.stockranker.api.controller;

import dev.starryeye.stockranker.api.controller.request.GetRankRequest;
import dev.starryeye.stockranker.api.facade.GetRankByTagUseCase;
import dev.starryeye.stockranker.api.facade.response.RankResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RequestMapping("/api/ranking")
@RestController
@RequiredArgsConstructor
public class GetRanksController {

    private final GetRankByTagUseCase getRankByTagUseCase;

    @GetMapping
    public Mono<RankResponse> getStocksByTag(
            @Valid @ModelAttribute GetRankRequest request
    ) {
        return getRankByTagUseCase.execute(request.getTag(), request.getSize());
    }
}
