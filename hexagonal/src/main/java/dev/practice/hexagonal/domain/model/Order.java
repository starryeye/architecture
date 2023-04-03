package dev.practice.hexagonal.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Order {

    private Long id;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
    }
}
