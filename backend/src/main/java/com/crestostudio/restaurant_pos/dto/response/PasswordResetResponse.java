package com.crestostudio.restaurant_pos.dto.response;

import java.util.UUID;

public record PasswordResetResponse(
        UUID userId,
        String email,
        String message,
        String temporaryPassword
) {
    public static PasswordResetResponse of(UUID userId, String email, String temporaryPassword) {
        return new PasswordResetResponse(
                userId,
                email,
                "Password reset successfully. A temporary password has been generated.",
                temporaryPassword
        );
    }
}
