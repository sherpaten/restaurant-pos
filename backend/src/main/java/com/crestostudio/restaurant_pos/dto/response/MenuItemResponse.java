package com.crestostudio.restaurant_pos.dto.response;

import com.crestostudio.restaurant_pos.entity.MenuItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID restaurantId,
        UUID categoryId,
        String categoryName,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Boolean isAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MenuItemResponse from(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getRestaurant() != null ? item.getRestaurant().getId() : null,
                item.getCategory() != null ? item.getCategory().getId() : null,
                item.getCategory() != null ? item.getCategory().getName() : null,
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.getIsAvailable(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
