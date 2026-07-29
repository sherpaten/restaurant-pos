package com.crestostudio.restaurant_pos.repository;

import com.crestostudio.restaurant_pos.entity.OtpVerification;
import com.crestostudio.restaurant_pos.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

    Optional<OtpVerification> findTopByUserEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            String userEmail, OtpPurpose purpose);

    long countByUserEmailAndPurposeAndCreatedAtAfter(
            String userEmail, OtpPurpose purpose, LocalDateTime after);

    @Modifying
    @Query("UPDATE OtpVerification o SET o.used = true WHERE o.userEmail = :email AND o.purpose = :purpose AND o.used = false")
    void invalidateAllForEmailAndPurpose(String email, OtpPurpose purpose);

    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :now")
    void deleteAllExpired(LocalDateTime now);

    List<OtpVerification> findAllByUserEmailAndPurpose(String userEmail, OtpPurpose purpose);
}
