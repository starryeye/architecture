package dev.practice.hexagonal.application.adapter.in.web.controller;

import dev.practice.hexagonal.application.adapter.in.dto.CreateOrderRequest;
import dev.practice.hexagonal.application.adapter.out.mapper.OrderMapper;
import dev.practice.hexagonal.application.service.OrderService;
import dev.practice.hexagonal.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request) {

        var Order = orderMapper.toDomain(request);
    }
}
