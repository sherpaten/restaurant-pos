package com.crestostudio.restaurant_pos.dto.response;

import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserRole role,
        UserStatus status,
        Boolean firstLogin,
        UUID restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getFirstLogin(),
                user.getRestaurant() != null ? user.getRestaurant().getId() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
