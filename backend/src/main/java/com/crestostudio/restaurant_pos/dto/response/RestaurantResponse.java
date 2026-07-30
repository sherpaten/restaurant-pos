package com.crestostudio.restaurant_pos.dto.response;

import com.crestostudio.restaurant_pos.entity.Restaurant;
import com.crestostudio.restaurant_pos.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String address,
        String panNumber,
        String vatNumber,
        BigDecimal vatRate,
        String logoUrl,
        Boolean isActive,
        String ownerName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RestaurantResponse from(Restaurant r) {
        String ownerName = r.getUsers() == null ? null : r.getUsers().stream()
                .filter(u -> u.getRole() == UserRole.OWNER)
                .map(u -> u.getFirstName() + (u.getLastName() != null && !u.getLastName().isBlank() ? " " + u.getLastName() : ""))
                .findFirst()
                .orElse(null);
        return from(r, ownerName);
    }

    public static RestaurantResponse from(Restaurant r, String ownerName) {
        return new RestaurantResponse(
                r.getId(),
                r.getName(),
                r.getEmail(),
                r.getPhone(),
                r.getAddress(),
                r.getPanNumber(),
                r.getVatNumber(),
                r.getVatRate(),
                r.getLogoUrl(),
                r.getIsActive(),
                ownerName,
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
