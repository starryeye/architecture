package dev.practice.hexagonal.application.adapter.out.mapper;

import dev.practice.hexagonal.application.adapter.in.dto.CreateOrderRequest;
import dev.practice.hexagonal.domain.model.Order;
import dev.practice.hexagonal.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public Order toDomain(CreateOrderRequest request) {
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();

        for(CreateOrderRequest.OrderItemDto orderItemDto : request.getOrderItemDtoList()) {
            OrderItem orderItem = new OrderItem();
        }
    }
}
