package dev.practice.hexagonal.application.adapter.in.dto;

import dev.practice.hexagonal.domain.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderRequest {
    private Long id;
    private OrderStatus orderStatus;
}
