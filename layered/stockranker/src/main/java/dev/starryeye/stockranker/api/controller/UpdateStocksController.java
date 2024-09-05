package dev.starryeye.stockranker.api.controller;

import dev.starryeye.stockranker.api.controller.request.UpdateStockRequest;
import dev.starryeye.stockranker.api.facade.UpdateStockUseCase;
import dev.starryeye.stockranker.domain.Stock;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/stocks")
@RestController
@RequiredArgsConstructor
public class UpdateStocksController {

    private final UpdateStockUseCase useCase;

    @PutMapping("/update")
    public Mono<Void> updateStocks(
            @Valid @RequestBody List<UpdateStockRequest> stockRequests
    ) {
        List<Stock> stocks = stockRequests.stream()
                .map(request -> Stock.builder()
                        .id(request.id())
                        .price(request.price())
                        .previousClosePrice(request.previousClosePrice())
                        .volume(request.volume())
                        .views(request.views())
                        .build())
                .toList();

        return useCase.execute(stocks);
    }
}
