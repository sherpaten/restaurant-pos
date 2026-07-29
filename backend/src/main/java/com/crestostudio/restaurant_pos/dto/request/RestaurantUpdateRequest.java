package com.crestostudio.restaurant_pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RestaurantUpdateRequest(
        @NotBlank(message = "Restaurant name is required")
        String name,

        @NotBlank(message = "Phone number is required")
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Address is required")
        String address,

        String panNumber,

        String vatNumber,

        @NotNull(message = "VAT rate is required")
        @DecimalMin(value = "0.00", message = "VAT rate must be greater than or equal to 0")
        BigDecimal vatRate,

        String logoUrl
) {}
