package dev.starryeye.stockranker.api.facade;

import dev.starryeye.stockranker.api.service.StockService;
import dev.starryeye.stockranker.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateStockUseCase {

    private final StockService stockService;

    @Transactional
    public Mono<Void> execute(List<Stock> stocks) {
        if (stocks.isEmpty()) {
            return Mono.empty();
        }

        // 비동기 실행
        stockService.updateStocks(stocks).subscribeOn(Schedulers.boundedElastic()).subscribe();
        return Mono.empty();
    }
}
