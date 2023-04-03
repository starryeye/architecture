package dev.practice.hexagonal.domain.port.in;

import dev.practice.hexagonal.domain.model.Order;

public interface CreateOrderUseCase {

    Order createOrder(Order order);
}
