package dev.starryeye.stockranker.domain;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockProvider {

    Flux<Stock> findAllByOrderByViewsDesc(Pageable pageable);
    Flux<Stock> findAllByOrderByPriceChangeRatioDesc(Pageable pageable);
    Flux<Stock> findAllByOrderByPriceChangeRatioAsc(Pageable pageable);
    Flux<Stock> findAllByOrderByVolumeDesc(Pageable pageable);
    Mono<Long> bulkUpdateStocks(List<Stock> stocks);
    Mono<Long> bulkInsertStocks(List<Stock> stocks);
    Mono<Stock> findById(Long id);
    Flux<Stock> findAllById(List<Long> ids);
}
