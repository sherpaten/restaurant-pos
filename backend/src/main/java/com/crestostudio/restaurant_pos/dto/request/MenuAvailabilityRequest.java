package com.crestostudio.restaurant_pos.dto.request;

import jakarta.validation.constraints.NotNull;

public record MenuAvailabilityRequest(
        @NotNull(message = "Availability status is required")
        Boolean isAvailable
) {}
