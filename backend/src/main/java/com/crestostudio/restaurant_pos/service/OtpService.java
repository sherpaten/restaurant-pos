package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.entity.OtpVerification;
import com.crestostudio.restaurant_pos.enums.OtpPurpose;
import com.crestostudio.restaurant_pos.exception.InvalidOtpException;
import com.crestostudio.restaurant_pos.exception.OtpExpiredException;
import com.crestostudio.restaurant_pos.exception.OtpRateLimitException;
import com.crestostudio.restaurant_pos.repository.OtpVerificationRepository;
import com.crestostudio.restaurant_pos.util.OtpGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private final OtpGenerator otpGenerator;
    private final int expirationMinutes;
    private final int maxAttempts;
    private final int rateLimitWindowMinutes;

    public OtpService(
            OtpVerificationRepository otpRepository,
            OtpGenerator otpGenerator,
            @Value("${app.otp.expiration-minutes}") int expirationMinutes,
            @Value("${app.otp.max-attempts}") int maxAttempts,
            @Value("${app.otp.rate-limit-window-minutes}") int rateLimitWindowMinutes) {
        this.otpRepository = otpRepository;
        this.otpGenerator = otpGenerator;
        this.expirationMinutes = expirationMinutes;
        this.maxAttempts = maxAttempts;
        this.rateLimitWindowMinutes = rateLimitWindowMinutes;
    }

    @Transactional
    public OtpVerification generateOtp(String email, OtpPurpose purpose) {
        enforceRateLimit(email, purpose);
        invalidatePreviousOtps(email, purpose);

        String code = otpGenerator.generate();
        OtpVerification otp = OtpVerification.builder()
                .userEmail(email)
                .otp(code)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .verified(false)
                .used(false)
                .build();

        OtpVerification saved = otpRepository.save(otp);
        log.info("OTP generated for {} purpose={}", email, purpose);
        return saved;
    }

    @Transactional
    public OtpVerification verifyOtp(String email, String code, OtpPurpose purpose) {
        OtpVerification otp = otpRepository
                .findTopByUserEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new InvalidOtpException("No active OTP found for this email"));

        if (otp.getUsed()) {
            throw new InvalidOtpException("OTP has already been used");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired. Please request a new one");
        }
        if (!otp.getOtp().equals(code)) {
            throw new InvalidOtpException("Invalid OTP");
        }

        otp.setVerified(true);
        otp.setUsed(true);
        return otpRepository.save(otp);
    }

    @Transactional
    public OtpVerification resendOtp(String email, OtpPurpose purpose) {
        return generateOtp(email, purpose);
    }

    private void enforceRateLimit(String email, OtpPurpose purpose) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(rateLimitWindowMinutes);
        long count = otpRepository.countByUserEmailAndPurposeAndCreatedAtAfter(email, purpose, windowStart);
        if (count >= maxAttempts) {
            throw new OtpRateLimitException(
                    "Too many OTP requests. Maximum " + maxAttempts + " requests allowed per " + rateLimitWindowMinutes + " minutes"
            );
        }
    }

    private void invalidatePreviousOtps(String email, OtpPurpose purpose) {
        otpRepository.invalidateAllForEmailAndPurpose(email, purpose);
    }
}
