package com.crestostudio.restaurant_pos.repository;

import com.crestostudio.restaurant_pos.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    boolean existsByMenuItemId(UUID menuItemId);

    long countByMenuItemId(UUID menuItemId);
}
