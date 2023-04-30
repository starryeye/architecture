package dev.practice.hexagonal.domain.port.in;

import dev.practice.hexagonal.domain.model.Order;

public interface UpdateOrderUseCase {

    Order updateOrder(Order order);
}
