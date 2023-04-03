package dev.practice.hexagonal.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItem {

    private Long id;
    private Long productId;
    private int quantity;

    public OrderItem() {}

    public OrderItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
