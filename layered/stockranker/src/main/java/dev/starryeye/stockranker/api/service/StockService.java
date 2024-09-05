package dev.starryeye.stockranker.api.service;

import dev.starryeye.stockranker.domain.StockProvider;
import dev.starryeye.stockranker.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockProvider stockProvider;

    // command
    @Transactional
    public Mono<Void> updateStocks(List<Stock> stocks) {
        if (stocks.isEmpty()) {
            return Mono.empty();
        }

        List<Long> stockIds = stocks.stream()
                .map(Stock::getId)
                .filter(Objects::nonNull)
                .toList();

        return stockProvider.findAllById(stockIds).collectList()
                .flatMap(existingStocks -> {
                    List<Stock> mergedStocks = mergeStocks(stocks, existingStocks);
                    return stockProvider.bulkUpdateStocks(mergedStocks);
                })
                .then();
    }

    @Transactional
    public Mono<Void> createStocks(List<Stock> stocks) {
        if (stocks.isEmpty()) {
            return Mono.empty();
        }

        List<Stock> given = stocks.stream()
                .map(request -> Stock.create(request.getCode(), request.getName(), request.getPrice(), request.getPreviousClosePrice(), request.getVolume(), request.getViews()))
                .toList();

        return stockProvider.bulkInsertStocks(given).then();
    }

    // query
    public Flux<Stock> findTop100PopularStock() {
        Pageable pageable = PageRequest.of(0, 100);
        return stockProvider.findAllByOrderByViewsDesc(pageable);
    }

    public Flux<Stock> findTop100RisingStock() {
        Pageable pageable = PageRequest.of(0, 100);
        return stockProvider.findAllByOrderByPriceChangeRatioDesc(pageable);
    }

    public Flux<Stock> findTop100FallingStock() {
        Pageable pageable = PageRequest.of(0, 100);
        return stockProvider.findAllByOrderByPriceChangeRatioAsc(pageable);
    }

    public Flux<Stock> findTop100VolumeStock() {
        Pageable pageable = PageRequest.of(0, 100);
        return stockProvider.findAllByOrderByVolumeDesc(pageable);
    }


    public Flux<Stock> getStocksByStockIds(List<Long> stockIds) {
        return stockProvider.findAllById(stockIds);
    }

    private List<Stock> mergeStocks(List<Stock> newStocks, List<Stock> existingStocks) {
        Map<Long, Stock> newStockMap = newStocks.stream()
                .collect(Collectors.toMap(Stock::getId, stock -> stock));

        return existingStocks.stream()
                .map(existingStock -> {
                    Stock newStock = newStockMap.get(existingStock.getId());
                    return existingStock.updateStockStatus(newStock.getPrice(), newStock.getPreviousClosePrice(), newStock.getVolume(), newStock.getViews());
                })
                .toList();
    }
}
