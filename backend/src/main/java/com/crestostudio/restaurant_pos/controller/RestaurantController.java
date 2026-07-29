package com.crestostudio.restaurant_pos.controller;

import com.crestostudio.restaurant_pos.dto.request.RestaurantUpdateRequest;
import com.crestostudio.restaurant_pos.dto.response.ApiResponse;
import com.crestostudio.restaurant_pos.dto.response.RestaurantResponse;
import com.crestostudio.restaurant_pos.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getRestaurant() {
        RestaurantResponse response = restaurantService.getRestaurant();
        return ResponseEntity.ok(ApiResponse.success("Restaurant details retrieved successfully", response));
    }

    @PutMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateRestaurant(
            @Valid @RequestBody RestaurantUpdateRequest request) {
        RestaurantResponse response = restaurantService.updateRestaurant(request);
        return ResponseEntity.ok(ApiResponse.success("Restaurant details updated successfully", response));
    }
}
