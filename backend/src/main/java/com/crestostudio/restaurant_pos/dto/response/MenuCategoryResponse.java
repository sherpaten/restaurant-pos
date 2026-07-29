package com.crestostudio.restaurant_pos.dto.response;

import com.crestostudio.restaurant_pos.entity.MenuCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record MenuCategoryResponse(
        UUID id,
        UUID restaurantId,
        String name,
        String description,
        Integer itemCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MenuCategoryResponse from(MenuCategory category) {
        return new MenuCategoryResponse(
                category.getId(),
                category.getRestaurant() != null ? category.getRestaurant().getId() : null,
                category.getName(),
                category.getDescription(),
                category.getMenuItems() != null ? category.getMenuItems().size() : 0,
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
