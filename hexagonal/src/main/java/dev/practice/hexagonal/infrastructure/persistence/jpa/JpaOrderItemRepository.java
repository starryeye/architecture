package dev.practice.hexagonal.infrastructure.persistence.jpa;

import dev.practice.hexagonal.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
