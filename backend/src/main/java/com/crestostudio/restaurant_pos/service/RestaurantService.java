package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.dto.request.RestaurantUpdateRequest;
import com.crestostudio.restaurant_pos.dto.response.RestaurantResponse;
import com.crestostudio.restaurant_pos.entity.Restaurant;
import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.exception.RestaurantAlreadyExistsException;
import com.crestostudio.restaurant_pos.repository.RestaurantRepository;
import com.crestostudio.restaurant_pos.repository.UserRepository;
import com.crestostudio.restaurant_pos.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            SecurityUtils securityUtils) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurant() {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        String ownerName = getOwnerName(restaurant);
        return RestaurantResponse.from(restaurant, ownerName);
    }

    @Transactional
    public RestaurantResponse updateRestaurant(RestaurantUpdateRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        if (!restaurant.getEmail().equalsIgnoreCase(request.email())
                && restaurantRepository.existsByEmailAndIdNot(request.email(), restaurant.getId())) {
            throw new RestaurantAlreadyExistsException("A restaurant with email " + request.email() + " already exists");
        }

        restaurant.setName(request.name());
        restaurant.setPhone(request.phone());
        restaurant.setEmail(request.email());
        restaurant.setAddress(request.address());
        restaurant.setPanNumber(request.panNumber());
        restaurant.setVatNumber(request.vatNumber());
        restaurant.setVatRate(request.vatRate());
        restaurant.setLogoUrl(request.logoUrl());

        Restaurant updated = restaurantRepository.save(restaurant);
        String ownerName = getOwnerName(updated);
        return RestaurantResponse.from(updated, ownerName);
    }

    private String getOwnerName(Restaurant restaurant) {
        if (restaurant.getUsers() != null && !restaurant.getUsers().isEmpty()) {
            return restaurant.getUsers().stream()
                    .filter(u -> u.getRole() == UserRole.OWNER)
                    .map(u -> u.getFirstName() + (u.getLastName() != null && !u.getLastName().isBlank() ? " " + u.getLastName() : ""))
                    .findFirst()
                    .orElse(null);
        }
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == UserRole.OWNER) {
            return currentUser.getFirstName() + (currentUser.getLastName() != null && !currentUser.getLastName().isBlank() ? " " + currentUser.getLastName() : "");
        }
        return userRepository.findAll().stream()
                .filter(u -> u.getRestaurant() != null && u.getRestaurant().getId().equals(restaurant.getId()) && u.getRole() == UserRole.OWNER)
                .map(u -> u.getFirstName() + (u.getLastName() != null && !u.getLastName().isBlank() ? " " + u.getLastName() : ""))
                .findFirst()
                .orElse(null);
    }
}
