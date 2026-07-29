package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.entity.RefreshToken;
import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.exception.RefreshTokenExpiredException;
import com.crestostudio.restaurant_pos.repository.RefreshTokenRepository;
import com.crestostudio.restaurant_pos.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final long refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String tokenValue = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .revoked(false)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String oldTokenValue, User user) {
        RefreshToken existing = findValidToken(oldTokenValue);
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);
        return createRefreshToken(user);
    }

    @Transactional(readOnly = true)
    public RefreshToken findValidToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RefreshTokenExpiredException("Refresh token not found"));

        if (token.getRevoked()) {
            throw new RefreshTokenExpiredException("Refresh token has been revoked");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpiredException("Refresh token has expired");
        }
        return token;
    }

    @Transactional
    public void revokeToken(String tokenValue) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(tokenValue);
        token.ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        log.info("Revoked all refresh tokens for user {}", userId);
    }
}
