package dev.practice.hexagonal.domain.port.out;

import dev.practice.hexagonal.domain.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem orderItem);
    List<OrderItem> findByOrderId(Long orderId);
}
