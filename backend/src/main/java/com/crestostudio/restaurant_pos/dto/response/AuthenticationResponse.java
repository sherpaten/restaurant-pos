package com.crestostudio.restaurant_pos.dto.response;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        UserResponse user,
        RestaurantResponse restaurant
) {}
