package dev.practice.hexagonal.application.service;

import dev.practice.hexagonal.domain.model.Order;
import dev.practice.hexagonal.domain.port.in.CreateOrderUseCase;
import dev.practice.hexagonal.domain.port.in.UpdateOrderUseCase;
import dev.practice.hexagonal.domain.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderService implements CreateOrderUseCase, UpdateOrderUseCase {

    private final OrderRepository orderRepository;


    @Transactional
    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Order not found with id: %d", id)));
    }

    @Transactional(readOnly = true)
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
