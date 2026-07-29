package com.crestostudio.restaurant_pos.dto.response;

import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserRole role,
        UserStatus status,
        Boolean firstLogin,
        LocalDateTime createdAt
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getFirstLogin(),
                user.getCreatedAt()
        );
    }
}
