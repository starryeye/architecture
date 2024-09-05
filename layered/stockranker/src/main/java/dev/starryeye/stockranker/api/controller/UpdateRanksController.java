package dev.starryeye.stockranker.api.controller;

import dev.starryeye.stockranker.api.controller.request.UpdateRankRequest;
import dev.starryeye.stockranker.api.facade.UpdateRankUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/api/ranking")
@RestController
@RequiredArgsConstructor
public class UpdateRanksController {

    private final UpdateRankUseCase useCase;

    @PostMapping("/update")
    public Mono<Void> updateRankByTag(
            @Valid @RequestBody UpdateRankRequest request
    ) {
        if(isInValid(request.tag()))
            return Mono.error(new IllegalArgumentException("Requested tag value is invalid tag"));
        return useCase.execute(request.tag());
    }

    private boolean isInValid(String tag) {
        return !tag.equals("ALL") && !tag.equals("POPULAR") && !tag.equals("RISING") && !tag.equals("FALLING") && !tag.equals("VOLUME");
    }
}
