package com.crestostudio.restaurant_pos.repository;

import com.crestostudio.restaurant_pos.entity.User;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndRestaurantId(String email, UUID restaurantId);

    boolean existsByRestaurantIdAndEmailAndDeletedAtIsNull(UUID restaurantId, String email);

    boolean existsByRestaurantIdAndPhoneAndDeletedAtIsNull(UUID restaurantId, String phone);

    boolean existsByRestaurantIdAndPhoneAndIdNotAndDeletedAtIsNull(UUID restaurantId, String phone, UUID id);

    Optional<User> findByIdAndRestaurantIdAndDeletedAtIsNull(UUID id, UUID restaurantId);

    Page<User> findByRestaurantIdAndDeletedAtIsNull(UUID restaurantId, Pageable pageable);

    Page<User> findByRestaurantIdAndRoleAndDeletedAtIsNull(UUID restaurantId, UserRole role, Pageable pageable);

    Page<User> findByRestaurantIdAndStatusAndDeletedAtIsNull(UUID restaurantId, UserStatus status, Pageable pageable);

    Page<User> findByRestaurantIdAndRoleAndStatusAndDeletedAtIsNull(UUID restaurantId, UserRole role, UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.restaurant.id = :restaurantId AND u.deletedAt IS NULL AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(@Param("restaurantId") UUID restaurantId,
                           @Param("keyword") String keyword,
                           Pageable pageable);
}
