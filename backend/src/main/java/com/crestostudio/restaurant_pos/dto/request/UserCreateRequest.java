package com.crestostudio.restaurant_pos.dto.request;

import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserCreateRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9+()\\s-]{7,20}$", message = "Invalid phone number format")
        String phone,

        @NotNull(message = "Role is required")
        UserRole role,

        @NotNull(message = "Status is required")
        UserStatus status
) {}
