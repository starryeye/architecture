package dev.starryeye.stockranker.domain;

import dev.starryeye.stockranker.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Table("STOCK")
public class Stock extends BaseEntity {

    @Id
    private final Long id;

    private final String code;
    private final String name;
    private final BigDecimal price;
    private final BigDecimal previousClosePrice;
    private final BigDecimal priceChangeRatio;
    private final Long volume;
    private final Long views;

    @Builder
    private Stock(LocalDateTime createdAt, LocalDateTime updatedAt, Long id, String code, String name, BigDecimal price, BigDecimal previousClosePrice, BigDecimal priceChangeRatio, Long volume, Long views) {
        super(createdAt, updatedAt);
        this.id = id;
        this.code = code;
        this.name = name;
        this.price = price;
        this.previousClosePrice = previousClosePrice;
        this.priceChangeRatio = priceChangeRatio;
        this.volume = volume;
        this.views = views;
    }

    public static Stock create(String code, String name, BigDecimal price, BigDecimal previousClosePrice, Long volume, Long views) {
        return Stock.builder()
                .createdAt(null)
                .updatedAt(null)
                .id(null)
                .code(code)
                .name(name)
                .price(price)
                .previousClosePrice(previousClosePrice)
                .priceChangeRatio(calculatePriceChangeRatio(price, previousClosePrice))
                .volume(volume)
                .views(views)
                .build();
    }

    public Stock updateStockStatus(BigDecimal newPrice, BigDecimal newPreviousClosePrice, Long newVolume, Long newViews) {
        return Stock.builder()
                .createdAt(getCreatedAt())
                .updatedAt(null)
                .id(getId())
                .code(getCode())
                .name(getName())
                .price(newPrice)
                .previousClosePrice(newPreviousClosePrice)
                .priceChangeRatio(calculatePriceChangeRatio(newPrice, newPreviousClosePrice))
                .volume(newVolume)
                .views(newViews)
                .build();
    }

    private static BigDecimal calculatePriceChangeRatio(BigDecimal price, BigDecimal previousClosePrice) {
        if (previousClosePrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Previous close price cannot be zero");
        }
        BigDecimal change = price.subtract(previousClosePrice);
        BigDecimal changePercentage = change.divide(previousClosePrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L));
        return changePercentage.setScale(2, RoundingMode.HALF_UP);
    }

}
