package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.dto.request.MenuAvailabilityRequest;
import com.crestostudio.restaurant_pos.dto.request.MenuItemRequest;
import com.crestostudio.restaurant_pos.dto.response.MenuItemResponse;
import com.crestostudio.restaurant_pos.entity.MenuCategory;
import com.crestostudio.restaurant_pos.entity.MenuItem;
import com.crestostudio.restaurant_pos.entity.Restaurant;
import com.crestostudio.restaurant_pos.exception.ResourceAlreadyExistsException;
import com.crestostudio.restaurant_pos.exception.ResourceNotFoundException;
import com.crestostudio.restaurant_pos.exception.ValidationException;
import com.crestostudio.restaurant_pos.repository.MenuCategoryRepository;
import com.crestostudio.restaurant_pos.repository.MenuItemRepository;
import com.crestostudio.restaurant_pos.repository.OrderItemRepository;
import com.crestostudio.restaurant_pos.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final SecurityUtils securityUtils;

    public MenuItemService(
            MenuItemRepository menuItemRepository,
            MenuCategoryRepository menuCategoryRepository,
            OrderItemRepository orderItemRepository,
            SecurityUtils securityUtils) {
        this.menuItemRepository = menuItemRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getAllItems(Pageable pageable) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        return menuItemRepository.findByRestaurantId(restaurant.getId(), pageable)
                .map(MenuItemResponse::from);
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getItemById(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        MenuItem item = menuItemRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));
        return MenuItemResponse.from(item);
    }

    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getItemsByCategory(UUID categoryId, Pageable pageable) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(categoryId, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        return menuItemRepository.findByRestaurantIdAndCategoryId(restaurant.getId(), category.getId(), pageable)
                .map(MenuItemResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<MenuItemResponse> searchItems(String keyword, Pageable pageable) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        String searchTerm = keyword == null ? "" : keyword.trim();
        return menuItemRepository.searchByRestaurantIdAndKeyword(restaurant.getId(), searchTerm, pageable)
                .map(MenuItemResponse::from);
    }

    @Transactional
    public MenuItemResponse createItem(MenuItemRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be greater than 0");
        }

        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(request.categoryId(), restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to your restaurant"));

        if (menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(restaurant.getId(), request.name().trim())) {
            throw new ResourceAlreadyExistsException("Menu item with name '" + request.name() + "' already exists in this restaurant");
        }

        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setCategory(category);
        item.setName(request.name().trim());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());
        item.setIsAvailable(request.isAvailable() != null ? request.isAvailable() : true);

        MenuItem saved = menuItemRepository.save(item);
        return MenuItemResponse.from(saved);
    }

    @Transactional
    public MenuItemResponse updateItem(UUID id, MenuItemRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        MenuItem item = menuItemRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));

        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be greater than 0");
        }

        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(request.categoryId(), restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to your restaurant"));

        if (menuItemRepository.existsByRestaurantIdAndNameIgnoreCaseAndIdNot(restaurant.getId(), request.name().trim(), id)) {
            throw new ResourceAlreadyExistsException("Menu item with name '" + request.name() + "' already exists in this restaurant");
        }

        item.setCategory(category);
        item.setName(request.name().trim());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());
        if (request.isAvailable() != null) {
            item.setIsAvailable(request.isAvailable());
        }

        MenuItem updated = menuItemRepository.save(item);
        return MenuItemResponse.from(updated);
    }

    @Transactional
    public MenuItemResponse updateAvailability(UUID id, MenuAvailabilityRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        MenuItem item = menuItemRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));

        item.setIsAvailable(request.isAvailable());
        MenuItem updated = menuItemRepository.save(item);
        return MenuItemResponse.from(updated);
    }

    @Transactional
    public boolean deleteItem(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        MenuItem item = menuItemRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));

        boolean hasBeenOrdered = orderItemRepository.existsByMenuItemId(id);

        if (hasBeenOrdered) {
            item.setIsAvailable(false);
            menuItemRepository.save(item);
            return false;
        } else {
            menuItemRepository.delete(item);
            return true;
        }
    }
}
