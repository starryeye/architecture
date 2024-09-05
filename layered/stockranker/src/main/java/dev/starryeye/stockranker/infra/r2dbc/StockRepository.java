package dev.starryeye.stockranker.infra.r2dbc;

import dev.starryeye.stockranker.domain.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockRepository extends R2dbcRepository<Stock, Long> {

    Flux<Stock> findAllByOrderByViewsDesc(Pageable pageable);
    Flux<Stock> findAllByOrderByPriceChangeRatioDesc(Pageable pageable);
    Flux<Stock> findAllByOrderByPriceChangeRatioAsc(Pageable pageable);
    Flux<Stock> findAllByOrderByVolumeDesc(Pageable pageable);

    Flux<Stock> findByIdIn(List<Long> ids);
    Mono<Stock> findByCode(String code);
}
