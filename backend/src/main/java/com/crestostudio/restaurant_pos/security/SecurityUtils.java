package com.crestostudio.restaurant_pos.security;

import com.crestostudio.restaurant_pos.entity.Restaurant;
import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.exception.ResourceNotFoundException;
import com.crestostudio.restaurant_pos.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Authenticated user context not found");
        }

        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String principalEmail) {
            email = principalEmail;
        } else {
            throw new ResourceNotFoundException("Invalid authentication principal");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public Restaurant getCurrentRestaurant() {
        User user = getCurrentUser();
        Restaurant restaurant = user.getRestaurant();
        if (restaurant == null) {
            throw new ResourceNotFoundException("Restaurant not found for current user");
        }
        return restaurant;
    }
}
