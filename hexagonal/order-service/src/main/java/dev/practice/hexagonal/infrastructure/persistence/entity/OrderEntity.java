package dev.practice.hexagonal.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderToken;
    private String receiverName;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems;

    public void addOrderItem(OrderItemEntity orderItemEntity) {
        orderItems.add(orderItemEntity);
        orderItemEntity.setOrder(this);
    }

    public void removeOrderItem(OrderItemEntity orderItemEntity) {
        orderItems.remove(orderItemEntity);
        orderItemEntity.setOrder(null);
    }


}
