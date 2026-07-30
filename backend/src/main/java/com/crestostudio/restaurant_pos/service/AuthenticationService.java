package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.dto.request.*;
import com.crestostudio.restaurant_pos.dto.response.AuthenticationResponse;
import com.crestostudio.restaurant_pos.dto.response.RestaurantResponse;
import com.crestostudio.restaurant_pos.dto.response.UserResponse;
import com.crestostudio.restaurant_pos.entity.*;
import com.crestostudio.restaurant_pos.enums.OtpPurpose;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;
import com.crestostudio.restaurant_pos.exception.*;
import com.crestostudio.restaurant_pos.repository.RestaurantRepository;
import com.crestostudio.restaurant_pos.repository.UserRepository;
import com.crestostudio.restaurant_pos.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final PendingRegistrationService pendingRegistrationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            OtpService otpService,
            EmailService emailService,
            PendingRegistrationService pendingRegistrationService,
            RefreshTokenService refreshTokenService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.pendingRegistrationService = pendingRegistrationService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }
        if (restaurantRepository.existsByEmail(request.restaurantEmail())) {
            throw new RestaurantAlreadyExistsException("A restaurant with this email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());
        OtpVerification otp = otpService.generateOtp(request.email(), OtpPurpose.REGISTRATION);
        pendingRegistrationService.save(request, passwordHash, otp);
        emailService.sendRegistrationOtp(request.email(), request.firstName(), otp.getOtp());

        log.info("Registration initiated for {}", request.email());
    }

    @Transactional
    public AuthenticationResponse verifyRegistration(RegistrationVerificationRequest request) {
        PendingRegistration pending = pendingRegistrationService.findByEmail(request.email());
        otpService.verifyOtp(request.email(), request.otp(), OtpPurpose.REGISTRATION);

        Restaurant restaurant = buildRestaurant(pending);
        restaurantRepository.save(restaurant);

        User user = buildOwner(pending, restaurant);
        userRepository.save(user);

        pendingRegistrationService.delete(pending);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user);

        log.info("Registration completed for {}", user.getEmail());
        return new AuthenticationResponse(
                accessToken,
                refreshToken.getToken(),
                UserResponse.from(user),
                RestaurantResponse.from(restaurant)
        );
    }

    @Transactional
    public AuthenticationResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getDeletedAt() != null) {
            throw new UserInactiveException("Your account has been deleted");
        }

        Restaurant restaurant = user.getRestaurant();
        if (!restaurant.getIsActive()) {
            throw new RestaurantInactiveException("Your restaurant account is inactive. Please contact support");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserInactiveException("Your account is inactive. Please contact your manager");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user);

        log.info("User logged in: {}", user.getEmail());
        return new AuthenticationResponse(
                accessToken,
                refreshToken.getToken(),
                UserResponse.from(user),
                RestaurantResponse.from(restaurant)
        );
    }

    @Transactional
    public AuthenticationResponse refresh(RefreshTokenRequest request) {
        RefreshToken existingToken = refreshTokenService.findValidToken(request.refreshToken());
        User user = existingToken.getUser();

        if (user.getDeletedAt() != null || user.getStatus() != UserStatus.ACTIVE) {
            throw new UserInactiveException("Your account is no longer active");
        }

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(request.refreshToken(), user);
        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthenticationResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                UserResponse.from(user),
                RestaurantResponse.from(user.getRestaurant())
        );
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revokeToken(request.refreshToken());
        log.info("User logged out");
    }

    @Transactional
    public void resendRegistrationOtp(ResendOtpRequest request) {
        PendingRegistration pending = pendingRegistrationService.findByEmail(request.email());
        OtpVerification otp = otpService.resendOtp(request.email(), OtpPurpose.REGISTRATION);
        emailService.sendRegistrationOtp(request.email(), pending.getFirstName(), otp.getOtp());
        log.info("Registration OTP resent to {}", request.email());
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (user.getDeletedAt() != null) {
            throw new InvalidCredentialsException("User not found");
        }
        return new AuthenticationResponse(null, null, UserResponse.from(user), RestaurantResponse.from(user.getRestaurant()));
    }

    private Restaurant buildRestaurant(PendingRegistration pending) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(pending.getRestaurantName());
        restaurant.setEmail(pending.getRestaurantEmail());
        restaurant.setPhone(pending.getRestaurantPhone());
        restaurant.setAddress(pending.getRestaurantAddress());
        restaurant.setPanNumber(pending.getPanNumber());
        restaurant.setVatNumber(pending.getVatNumber());
        restaurant.setIsActive(true);
        return restaurant;
    }

    private User buildOwner(PendingRegistration pending, Restaurant restaurant) {
        User user = new User();
        user.setFirstName(pending.getFirstName());
        user.setLastName(pending.getLastName());
        user.setEmail(pending.getEmail());
        user.setPhone(pending.getPhone());
        user.setPasswordHash(pending.getPasswordHash());
        user.setRole(UserRole.OWNER);
        user.setStatus(UserStatus.ACTIVE);
        user.setFirstLogin(false);
        user.setRestaurant(restaurant);
        return user;
    }
}
