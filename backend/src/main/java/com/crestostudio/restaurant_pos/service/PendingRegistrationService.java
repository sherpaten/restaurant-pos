package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.dto.request.RegistrationRequest;
import com.crestostudio.restaurant_pos.entity.OtpVerification;
import com.crestostudio.restaurant_pos.entity.PendingRegistration;
import com.crestostudio.restaurant_pos.repository.PendingRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class PendingRegistrationService {

    private final PendingRegistrationRepository pendingRegistrationRepository;

    public PendingRegistrationService(PendingRegistrationRepository pendingRegistrationRepository) {
        this.pendingRegistrationRepository = pendingRegistrationRepository;
    }

    @Transactional
    public PendingRegistration save(RegistrationRequest request, String passwordHash, OtpVerification otpVerification) {
        Optional<PendingRegistration> existing = pendingRegistrationRepository.findByEmail(request.email());
        existing.ifPresent(pendingRegistrationRepository::delete);

        PendingRegistration pending = PendingRegistration.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .passwordHash(passwordHash)
                .restaurantName(request.restaurantName())
                .restaurantEmail(request.restaurantEmail())
                .restaurantPhone(request.restaurantPhone())
                .restaurantAddress(request.restaurantAddress())
                .panNumber(request.panNumber())
                .vatNumber(request.vatNumber())
                .otpVerification(otpVerification)
                .build();

        return pendingRegistrationRepository.save(pending);
    }

    @Transactional(readOnly = true)
    public PendingRegistration findByEmail(String email) {
        return pendingRegistrationRepository.findByEmail(email)
                .orElseThrow(() -> new com.crestostudio.restaurant_pos.exception.ValidationException(
                        "No pending registration found for this email"));
    }

    @Transactional
    public void delete(PendingRegistration pendingRegistration) {
        pendingRegistrationRepository.delete(pendingRegistration);
        log.info("Deleted pending registration for {}", pendingRegistration.getEmail());
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return pendingRegistrationRepository.existsByEmail(email);
    }
}
