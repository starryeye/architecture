package dev.starryeye.stockranker.api.controller;

import dev.starryeye.stockranker.api.controller.request.CreateStockRequest;
import dev.starryeye.stockranker.api.facade.CreateStockUseCase;
import dev.starryeye.stockranker.domain.Stock;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/stocks")
@RestController
@RequiredArgsConstructor
public class CreateStocksController {

    private final CreateStockUseCase useCase;

    @PostMapping("/new")
    public Mono<Void> createStocks(
            @Valid @RequestBody List<CreateStockRequest> stockRequests
    ) {
        List<Stock> stocks = stockRequests.stream()
                .map(request -> Stock.builder()
                        .code(request.code())
                        .name(request.name())
                        .price(request.price())
                        .previousClosePrice(request.previousClosePrice())
                        .volume(request.volume())
                        .views(request.views())
                        .build())
                .toList();

        return useCase.execute(stocks);
    }
}
