package dev.practice.hexagonal.application.adapter.in.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    private Long customerId;
    private List<OrderItemDto> orderItemDtoList;

    @Getter
    @Setter
    public static class OrderItemDto {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
    }
}
