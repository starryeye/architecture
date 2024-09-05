package dev.starryeye.stockranker.api.facade;

import dev.starryeye.stockranker.api.facade.response.RankResponse;
import dev.starryeye.stockranker.api.facade.response.StockDto;
import dev.starryeye.stockranker.api.service.RankService;
import dev.starryeye.stockranker.api.service.StockService;
import dev.starryeye.stockranker.domain.Rank;
import dev.starryeye.stockranker.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GetRankByTagUseCase {

    private final RankService rankService;
    private final StockService stockService;

    public Mono<RankResponse> execute(String tag, Integer size) {
        return rankService.getRanksByTag(tag, size)
                .collectList()
                .flatMap(ranks -> {
                    List<Long> stockIds = extractStockIds(ranks);
                    return stockService.getStocksByStockIds(stockIds)
                            .collectMap(Stock::getId)
                            .map(stockMap -> createStockDtos(ranks, stockMap))
                            .map(stockDtos -> createRankResponse(stockDtos, tag, size));
                });
    }

    private List<Long> extractStockIds(List<Rank> ranks) {
        return ranks.stream()
                .map(Rank::getStockId)
                .toList();
    }

    private List<StockDto> createStockDtos(List<Rank> ranks, Map<Long, Stock> stockMap) {
        return ranks.stream()
                .map(rank -> {
                    Stock stock = stockMap.get(rank.getStockId());
                    return new StockDto(rank.getRank(), stock.getCode(), stock.getName(), stock.getPrice(), stock.getPreviousClosePrice(), stock.getPriceChangeRatio(), stock.getVolume(), stock.getViews());
                })
                .toList();
    }

    private RankResponse createRankResponse(List<StockDto> stockDtos, String tag, Integer size) {
        int nextSize = size >= 100 ? 100 : size + 20;
        return new RankResponse(stockDtos, tag, stockDtos.size(), nextSize);
    }
}
