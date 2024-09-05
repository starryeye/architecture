package dev.starryeye.stockranker.api.service;

import dev.starryeye.stockranker.domain.Rank;
import dev.starryeye.stockranker.domain.RankProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankProvider rankProvider;

    // command
    @Transactional
    public Mono<Void> updateRankings(String tag, List<Rank> createdRanks) {
        Pageable pageable = PageRequest.ofSize(100);
        return rankProvider.findByTagOrderByRankAsc(tag, pageable)
                .collectList()
                .flatMap(existingRanks -> {
                    List<Rank> updatedRanks = mergeRanks(createdRanks, existingRanks);

                    List<Rank> ranksToInsert = updatedRanks.stream()
                            .filter(rank -> rank.getId() == null)
                            .toList();

                    List<Rank> ranksToUpdate = updatedRanks.stream()
                            .filter(rank -> rank.getId() != null)
                            .toList();

                    return rankProvider.bulkInsertRanks(ranksToInsert)
                            .zipWith(rankProvider.bulkUpdateRanks(ranksToUpdate))
                            .map(tuple -> tuple.getT1() + tuple.getT2());
                }).then();
    }

    private List<Rank> mergeRanks(List<Rank> newRanks, List<Rank> existingRanks) {
        Map<Integer, Rank> existingRankMap = existingRanks.stream()
                .collect(Collectors.toMap(Rank::getRank, rank -> rank));

        return newRanks.stream()
                .map(newRank -> {
                    Rank existingRank = existingRankMap.get(newRank.getRank());
                    if (existingRank == null) {
                        return newRank;
                    }
                    return existingRank.changeStockId(newRank.getStockId());
                })
                .toList();
    }

    // query
    public Flux<Rank> getRanksByTag(String tag, Integer size) {
        if(size < 1 || size > 100) {
            return Flux.error(new IllegalArgumentException("Size must be at least 1 and at most 100"));
        }

        Pageable pageable = PageRequest.of(0, size);
        return rankProvider.findByTagOrderByRankAsc(tag, pageable);
    }
}
