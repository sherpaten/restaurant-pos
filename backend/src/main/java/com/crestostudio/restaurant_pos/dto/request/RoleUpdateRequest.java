package com.crestostudio.restaurant_pos.dto.request;

import com.crestostudio.restaurant_pos.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "Role is required")
        UserRole role
) {}
