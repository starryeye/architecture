package dev.starryeye.stockranker.api.facade;

import dev.starryeye.stockranker.api.service.StockService;
import dev.starryeye.stockranker.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateStockUseCase {

    private final StockService stockService;

    @Transactional
    public Mono<Void> execute(List<Stock> stocks) {
        if (stocks.isEmpty()) {
            return Mono.empty();
        }

        return stockService.createStocks(stocks).then();
    }
}
