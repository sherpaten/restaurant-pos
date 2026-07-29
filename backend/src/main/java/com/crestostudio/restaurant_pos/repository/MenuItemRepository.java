package com.crestostudio.restaurant_pos.repository;

import com.crestostudio.restaurant_pos.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    List<MenuItem> findByRestaurantId(UUID restaurantId);

    Page<MenuItem> findByRestaurantId(UUID restaurantId, Pageable pageable);

    Optional<MenuItem> findByIdAndRestaurantId(UUID id, UUID restaurantId);

    Page<MenuItem> findByRestaurantIdAndCategoryId(UUID restaurantId, UUID categoryId, Pageable pageable);

    List<MenuItem> findByRestaurantIdAndCategoryId(UUID restaurantId, UUID categoryId);

    Page<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId, Pageable pageable);

    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId);

    boolean existsByRestaurantIdAndNameIgnoreCase(UUID restaurantId, String name);

    boolean existsByRestaurantIdAndNameIgnoreCaseAndIdNot(UUID restaurantId, String name, UUID id);

    boolean existsByCategoryId(UUID categoryId);

    long countByRestaurantId(UUID restaurantId);

    long countByCategoryId(UUID categoryId);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MenuItem> searchByRestaurantIdAndKeyword(@Param("restaurantId") UUID restaurantId, 
                                                   @Param("keyword") String keyword, 
                                                   Pageable pageable);
}
