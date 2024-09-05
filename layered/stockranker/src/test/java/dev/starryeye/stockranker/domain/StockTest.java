package dev.starryeye.stockranker.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @DisplayName("Stock 엔티티를 생성한다.")
    @Test
    void create() {

        // given
        String code = "TEST";
        String name = "Test Stock";
        BigDecimal price = BigDecimal.valueOf(150L);
        BigDecimal previousClosePrice = BigDecimal.valueOf(100L);
        Long volume = 1000L;
        Long views = 500L;

        // when
        Stock stock = Stock.create(code, name, price, previousClosePrice, volume, views);

        // then
        assertThat(stock.getId()).isNull();
        assertThat(stock.getCode()).isEqualTo(code);
        assertThat(stock.getName()).isEqualTo(name);
        assertThat(stock.getPrice()).isEqualTo(price);
        assertThat(stock.getPreviousClosePrice()).isEqualTo(previousClosePrice);
        assertThat(stock.getPriceChangeRatio()).isEqualTo(new BigDecimal("50.00"));
        assertThat(stock.getVolume()).isEqualTo(volume);
        assertThat(stock.getViews()).isEqualTo(views);
    }

    @DisplayName("Stock 엔티티를 업데이트한다.")
    @Test
    void updateStockStatus() {

        // given
        String code = "TEST";
        String name = "Test Stock";
        BigDecimal price = BigDecimal.valueOf(100L);
        BigDecimal previousClosePrice = BigDecimal.valueOf(100L);
        Long volume = 1000L;
        Long views = 500L;

        Stock stock = Stock.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .code(code)
                .name(name)
                .price(price)
                .previousClosePrice(previousClosePrice)
                .priceChangeRatio(new BigDecimal("0.00"))
                .volume(volume)
                .views(views)
                .build();

        BigDecimal newPrice = BigDecimal.valueOf(200L);
        BigDecimal newPreviousClosePrice = BigDecimal.valueOf(150L);
        Long newVolume = 2000L;
        Long newViews = 1000L;

        // when
        Stock updatedStock = stock.updateStockStatus(newPrice, newPreviousClosePrice, newVolume, newViews);

        // then
        assertThat(updatedStock.getCreatedAt()).isEqualTo(stock.getCreatedAt());
        assertThat(updatedStock.getUpdatedAt()).isNull();
        assertThat(updatedStock.getId()).isEqualTo(stock.getId());
        assertThat(updatedStock.getCode()).isEqualTo(stock.getCode());
        assertThat(updatedStock.getName()).isEqualTo(stock.getName());
        assertThat(updatedStock.getPrice()).isEqualTo(newPrice);
        assertThat(updatedStock.getPreviousClosePrice()).isEqualTo(newPreviousClosePrice);
        assertThat(updatedStock.getPriceChangeRatio()).isEqualTo(new BigDecimal("33.33"));
        assertThat(updatedStock.getVolume()).isEqualTo(newVolume);
        assertThat(updatedStock.getViews()).isEqualTo(newViews);
    }

    @DisplayName("Stock 엔티티를 생성할때, previousClosePrice 가 0 이면 에러가 발생한다.")
    @Test
    void previousClosePriceIsZero() {

        // given
        String code = "TEST";
        String name = "Test Stock";
        BigDecimal price = BigDecimal.valueOf(150L);
        BigDecimal previousClosePrice = BigDecimal.ZERO;
        Long volume = 1000L;
        Long views = 500L;

        // when
        assertThatThrownBy(
                () -> Stock.create(code, name, price, previousClosePrice, volume, views)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Previous close price cannot be zero");
    }

}