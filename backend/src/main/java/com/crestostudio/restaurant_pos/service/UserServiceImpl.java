package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.dto.request.RoleUpdateRequest;
import com.crestostudio.restaurant_pos.dto.request.UserCreateRequest;
import com.crestostudio.restaurant_pos.dto.request.UserUpdateRequest;
import com.crestostudio.restaurant_pos.dto.response.PasswordResetResponse;
import com.crestostudio.restaurant_pos.dto.response.UserResponse;
import com.crestostudio.restaurant_pos.dto.response.UserSummaryResponse;
import com.crestostudio.restaurant_pos.entity.Restaurant;
import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;
import com.crestostudio.restaurant_pos.exception.ResourceNotFoundException;
import com.crestostudio.restaurant_pos.exception.UserAlreadyExistsException;
import com.crestostudio.restaurant_pos.exception.ValidationException;
import com.crestostudio.restaurant_pos.repository.UserRepository;
import com.crestostudio.restaurant_pos.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordGeneratorService passwordGeneratorService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(
            UserRepository userRepository,
            SecurityUtils securityUtils,
            PasswordGeneratorService passwordGeneratorService,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.passwordGeneratorService = passwordGeneratorService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public UserResponse createEmployee(UserCreateRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        if (request.role() == UserRole.OWNER) {
            throw new ValidationException("Cannot create an employee with OWNER role");
        }

        if (userRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new UserAlreadyExistsException("An account with email " + request.email() + " already exists");
        }

        if (userRepository.existsByRestaurantIdAndPhoneAndDeletedAtIsNull(restaurant.getId(), request.phone().trim())) {
            throw new ValidationException("An account with phone number " + request.phone() + " already exists in your restaurant");
        }

        String tempPassword = passwordGeneratorService.generateTemporaryPassword();
        String passwordHash = passwordEncoder.encode(tempPassword);

        User user = new User();
        user.setRestaurant(restaurant);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName() != null ? request.lastName().trim() : null);
        user.setEmail(request.email().trim().toLowerCase());
        user.setPhone(request.phone().trim());
        user.setRole(request.role());
        user.setStatus(request.status());
        user.setFirstLogin(true);
        user.setPasswordHash(passwordHash);

        User savedUser = userRepository.save(user);
        log.info("Employee created successfully with ID: {} in restaurant: {}", savedUser.getId(), restaurant.getId());

        emailService.sendWelcomeEmail(
                savedUser.getEmail(),
                savedUser.getFirstName(),
                restaurant.getName(),
                savedUser.getRole(),
                tempPassword,
                "http://localhost:3000/login"
        );

        return UserResponse.from(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> getAllEmployees(Pageable pageable, UserRole role, UserStatus status) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        UUID restaurantId = restaurant.getId();

        Page<User> users;
        if (role != null && status != null) {
            users = userRepository.findByRestaurantIdAndRoleAndStatusAndDeletedAtIsNull(restaurantId, role, status, pageable);
        } else if (role != null) {
            users = userRepository.findByRestaurantIdAndRoleAndDeletedAtIsNull(restaurantId, role, pageable);
        } else if (status != null) {
            users = userRepository.findByRestaurantIdAndStatusAndDeletedAtIsNull(restaurantId, status, pageable);
        } else {
            users = userRepository.findByRestaurantIdAndDeletedAtIsNull(restaurantId, pageable);
        }

        return users.map(UserSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getEmployeeById(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        User user = userRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse updateEmployee(UUID id, UserUpdateRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        User user = userRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (user.getRole() == UserRole.OWNER && request.role() != UserRole.OWNER) {
            throw new ValidationException("OWNER role cannot be modified");
        }

        if (request.role() == UserRole.OWNER && user.getRole() != UserRole.OWNER) {
            throw new ValidationException("OWNER role cannot be assigned");
        }

        if (userRepository.existsByRestaurantIdAndPhoneAndIdNotAndDeletedAtIsNull(restaurant.getId(), request.phone().trim(), id)) {
            throw new ValidationException("An account with phone number " + request.phone() + " already exists in your restaurant");
        }

        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName() != null ? request.lastName().trim() : null);
        user.setPhone(request.phone().trim());
        user.setStatus(request.status());
        user.setRole(request.role());

        User updatedUser = userRepository.save(user);
        log.info("Employee updated successfully with ID: {}", updatedUser.getId());
        return UserResponse.from(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse activateEmployee(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        User user = userRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        user.setStatus(UserStatus.ACTIVE);
        User updatedUser = userRepository.save(user);
        log.info("Employee activated with ID: {}", updatedUser.getId());
        return UserResponse.from(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse deactivateEmployee(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        User user = userRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (user.getRole() == UserRole.OWNER) {
            throw new ValidationException("OWNER account cannot be deactivated");
        }

        user.setStatus(UserStatus.INACTIVE);
        User updatedUser = userRepository.save(user);
        log.info("Employee deactivated with ID: {}", updatedUser.getId());
        return UserResponse.from(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse changeRole(UUID id, RoleUpdateRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        User user = userRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (user.getRole() == UserRole.OWNER) {
            throw new ValidationException("OWNER role cannot be modified");
        }

        if (request.role() == UserRole.OWNER) {
            throw new ValidationException("OWNER role cannot be assigned");
        }

        user.setRole(request.role());
        User updatedUser = userRepository.save(user);
        log.info("Employee role changed to {} for ID: {}", request.role(), updatedUser.getId());
        return UserResponse.from(updatedUser);
    }

    @Override
    @Transactional
    public PasswordResetResponse resetPassword(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        User user = userRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (user.getRole() == UserRole.OWNER) {
            throw new ValidationException("OWNER password cannot be reset via this endpoint");
        }

        String tempPassword = passwordGeneratorService.generateTemporaryPassword();
        String passwordHash = passwordEncoder.encode(tempPassword);

        user.setPasswordHash(passwordHash);
        user.setFirstLogin(true);
        userRepository.save(user);
        log.info("Employee password reset successfully for ID: {}", user.getId());

        emailService.sendEmployeePasswordResetEmail(
                user.getEmail(),
                user.getFirstName(),
                restaurant.getName(),
                tempPassword,
                "http://localhost:3000/login"
        );

        return PasswordResetResponse.of(user.getId(), user.getEmail(), tempPassword);
    }

    @Override
    @Transactional
    public void softDeleteEmployee(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        User user = userRepository.findByIdAndRestaurantIdAndDeletedAtIsNull(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (user.getRole() == UserRole.OWNER) {
            throw new ValidationException("OWNER account cannot be deleted");
        }

        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Employee soft deleted with ID: {}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> searchEmployees(String keyword, Pageable pageable) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        String searchTerm = keyword == null ? "" : keyword.trim();
        return userRepository.searchUsers(restaurant.getId(), searchTerm, pageable)
                .map(UserSummaryResponse::from);
    }
}
