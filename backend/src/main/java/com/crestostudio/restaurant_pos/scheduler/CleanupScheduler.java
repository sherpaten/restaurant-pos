package com.crestostudio.restaurant_pos.scheduler;

import com.crestostudio.restaurant_pos.repository.OtpVerificationRepository;
import com.crestostudio.restaurant_pos.repository.PendingRegistrationRepository;
import com.crestostudio.restaurant_pos.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CleanupScheduler {

    private final OtpVerificationRepository otpVerificationRepository;
    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final int otpExpirationMinutes;

    public CleanupScheduler(
            OtpVerificationRepository otpVerificationRepository,
            PendingRegistrationRepository pendingRegistrationRepository,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.otp.expiration-minutes}") int otpExpirationMinutes) {
        this.otpVerificationRepository = otpVerificationRepository;
        this.pendingRegistrationRepository = pendingRegistrationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.otpExpirationMinutes = otpExpirationMinutes;
    }

    @Scheduled(fixedRateString = "PT10M")
    @Transactional
    public void cleanupExpiredOtps() {
        otpVerificationRepository.deleteAllExpired(LocalDateTime.now());
        log.debug("Cleaned up expired OTPs");
    }

    @Scheduled(fixedRateString = "PT15M")
    @Transactional
    public void cleanupExpiredPendingRegistrations() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(otpExpirationMinutes + 5);
        pendingRegistrationRepository.deleteAllExpiredBefore(cutoff);
        log.debug("Cleaned up expired pending registrations");
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredAndRevokedRefreshTokens() {
        refreshTokenRepository.deleteAllExpiredOrRevoked(LocalDateTime.now());
        log.debug("Cleaned up expired/revoked refresh tokens");
    }
}
