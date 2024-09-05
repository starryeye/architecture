package dev.starryeye.stockranker.infra;

import dev.starryeye.stockranker.domain.Stock;
import dev.starryeye.stockranker.domain.StockProvider;
import dev.starryeye.stockranker.infra.r2dbc.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockProviderImpl implements StockProvider {

    private final StockRepository stockRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Flux<Stock> findAllByOrderByViewsDesc(Pageable pageable) {
        return stockRepository.findAllByOrderByViewsDesc(pageable);
    }

    @Override
    public Flux<Stock> findAllByOrderByPriceChangeRatioDesc(Pageable pageable) {
        return stockRepository.findAllByOrderByPriceChangeRatioDesc(pageable);
    }

    @Override
    public Flux<Stock> findAllByOrderByPriceChangeRatioAsc(Pageable pageable) {
        return stockRepository.findAllByOrderByPriceChangeRatioAsc(pageable);
    }

    @Override
    public Flux<Stock> findAllByOrderByVolumeDesc(Pageable pageable) {
        return stockRepository.findAllByOrderByVolumeDesc(pageable);
    }

    @Override
    public Mono<Stock> findById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public Flux<Stock> findAllById(List<Long> ids) {
        return stockRepository.findAllById(ids);
    }

    @Override
    public Mono<Long> bulkUpdateStocks(List<Stock> stocks) {
        LocalDateTime now = LocalDateTime.now();
        return r2dbcEntityTemplate.getDatabaseClient()
                .inConnectionMany(connection -> Flux.fromIterable(stocks)
                        .flatMap(stock -> connection.createStatement(
                                        String.format("UPDATE STOCK SET name = '%s', price = %s, previous_close_price = %s, price_change_ratio = %s, volume = %d, views = %d, updated_at = '%s' WHERE code = '%s'",
                                                stock.getName(),
                                                stock.getPrice(),
                                                stock.getPreviousClosePrice(),
                                                stock.getPriceChangeRatio(),
                                                stock.getVolume(),
                                                stock.getViews(),
                                                now,
                                                stock.getCode()))
                                .execute()))
                .then(Mono.just((long) stocks.size()));
    }

    @Override
    public Mono<Long> bulkInsertStocks(List<Stock> stocks) {
        LocalDateTime now = LocalDateTime.now();
        String sql = "INSERT INTO STOCK (code, name, price, previous_close_price, price_change_ratio, volume, views, created_at, updated_at) VALUES ";
        String values = stocks.stream()
                .map(stock -> String.format("('%s', '%s', %s, %s, %s, %d, %d, '%s', '%s')",
                        stock.getCode(),
                        stock.getName(),
                        stock.getPrice(),
                        stock.getPreviousClosePrice(),
                        stock.getPriceChangeRatio(),
                        stock.getVolume(),
                        stock.getViews(),
                        now,
                        now))
                .collect(Collectors.joining(", "));
        sql += values;

        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .fetch()
                .rowsUpdated()
                .map(Long::valueOf);
    }
}
