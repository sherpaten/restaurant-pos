package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.entity.OtpVerification;
import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.enums.OtpPurpose;
import com.crestostudio.restaurant_pos.exception.InvalidCredentialsException;
import com.crestostudio.restaurant_pos.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            UserRepository userRepository,
            OtpService otpService,
            EmailService emailService,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initiateForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("No account found with this email"));

        OtpVerification otp = otpService.generateOtp(email, OtpPurpose.PASSWORD_RESET);
        String recipientName = user.getFirstName();
        emailService.sendPasswordResetOtp(email, recipientName, otp.getOtp());
        log.info("Password reset OTP sent to {}", email);
    }

    @Transactional
    public void verifyForgotPasswordOtp(String email, String code) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("No account found with this email"));
        otpService.verifyOtp(email, code, OtpPurpose.PASSWORD_RESET);
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("No account found with this email"));

        otpService.verifyOtp(email, otp, OtpPurpose.PASSWORD_RESET);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        refreshTokenService.revokeAllUserTokens(user.getId());
        log.info("Password reset successfully for {}", email);
    }
}
