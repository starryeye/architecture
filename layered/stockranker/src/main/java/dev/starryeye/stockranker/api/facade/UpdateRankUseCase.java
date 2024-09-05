package dev.starryeye.stockranker.api.facade;

import dev.starryeye.stockranker.api.service.RankService;
import dev.starryeye.stockranker.api.service.StockService;
import dev.starryeye.stockranker.domain.Rank;
import dev.starryeye.stockranker.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class UpdateRankUseCase {

    private final StockService stockService;
    private final RankService rankService;

    public Mono<Void> execute(String tag) {

        return switch (tag.toUpperCase()) {
            case "ALL" -> updateAllRankings();
            case "POPULAR" -> updateRanking("POPULAR", stockService.findTop100PopularStock());
            case "RISING" -> updateRanking("RISING", stockService.findTop100RisingStock());
            case "FALLING" -> updateRanking("FALLING", stockService.findTop100FallingStock());
            case "VOLUME" -> updateRanking("VOLUME", stockService.findTop100VolumeStock());
            default -> Mono.error(new IllegalArgumentException("Invalid tag: " + tag));
        };
    }

    private Mono<Void> updateAllRankings() {
        return Flux.merge(
                updateRanking("POPULAR", stockService.findTop100PopularStock()),
                updateRanking("RISING", stockService.findTop100RisingStock()),
                updateRanking("FALLING", stockService.findTop100FallingStock()),
                updateRanking("VOLUME", stockService.findTop100VolumeStock())
        ).then();
    }

    private Mono<Void> updateRanking(String tag, Flux<Stock> sortedStocks) {
        return sortedStocks.index()
                .map(tuple -> Rank.create(tuple.getT2().getId(), tag, tuple.getT1().intValue() + 1))
                .collectList()
                .flatMap(ranks -> rankService.updateRankings(tag, ranks))
                .subscribeOn(Schedulers.boundedElastic()); // 비동기


        /**
         * 참고
         * BoundedElastic 은 블로킹 작업에 사용하라고 보통 말하는데
         * 엥? 지금 webflux 에서 아주 잘 구현중이라 모두 비동기 논블로킹인데..
         * 그럼 바로 @Async, CompletableFuture 처럼 호출된 작업은 다른스레드로 비동기 처리하고
         * 호출한 스레드는 즉시 응답받도록은 못하나..?
         *
         * -> 호출된 작업을 기다리는게 동기 작업인 것 이다. 즉, 모든 곳에 비동기 논블로킹이라도
         * 관점에 따라 달라지는 것이다.
         * 여기서 boundedElastic 을 사용하는 것응 맞는 방법이다. (인듯..)
         */
    }
}
