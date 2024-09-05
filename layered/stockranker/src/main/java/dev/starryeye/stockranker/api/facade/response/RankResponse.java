package dev.starryeye.stockranker.api.facade.response;

import java.util.List;

public record RankResponse(
        List<StockDto> stocks,
        String tag,
        Integer totalCount,
        Integer nextSize
) {
}
