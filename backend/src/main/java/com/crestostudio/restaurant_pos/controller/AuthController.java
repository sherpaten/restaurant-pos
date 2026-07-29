package com.crestostudio.restaurant_pos.controller;

import com.crestostudio.restaurant_pos.dto.request.*;
import com.crestostudio.restaurant_pos.dto.response.ApiResponse;
import com.crestostudio.restaurant_pos.dto.response.AuthenticationResponse;
import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.repository.UserRepository;
import com.crestostudio.restaurant_pos.service.AuthenticationService;
import com.crestostudio.restaurant_pos.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;

    public AuthController(
            AuthenticationService authenticationService,
            PasswordResetService passwordResetService,
            UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegistrationRequest request) {
        authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration initiated. Please check your email for the OTP."));
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verifyRegistration(
            @Valid @RequestBody RegistrationVerificationRequest request) {
        AuthenticationResponse response = authenticationService.verifyRegistration(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful. Welcome!", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResponse response = authenticationService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PostMapping("/resend-registration-otp")
    public ResponseEntity<ApiResponse<Void>> resendRegistrationOtp(@Valid @RequestBody ResendOtpRequest request) {
        authenticationService.resendRegistrationOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP resent successfully. Please check your email."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.initiateForgotPassword(request.email());
        return ResponseEntity.ok(ApiResponse.success("If an account exists with this email, an OTP has been sent."));
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<ApiResponse<Void>> verifyForgotPassword(@Valid @RequestBody VerifyForgotOtpRequest request) {
        passwordResetService.verifyForgotPasswordOtp(request.email(), request.otp());
        return ResponseEntity.ok(ApiResponse.success("OTP verified. You may now reset your password."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.email(), request.otp(), request.newPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please log in with your new password."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        AuthenticationResponse response = authenticationService.getCurrentUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", response));
    }
}
