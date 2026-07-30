package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.dto.request.MenuCategoryRequest;
import com.crestostudio.restaurant_pos.dto.response.MenuCategoryResponse;
import com.crestostudio.restaurant_pos.entity.MenuCategory;
import com.crestostudio.restaurant_pos.entity.Restaurant;
import com.crestostudio.restaurant_pos.exception.ResourceAlreadyExistsException;
import com.crestostudio.restaurant_pos.exception.ResourceNotFoundException;
import com.crestostudio.restaurant_pos.exception.ValidationException;
import com.crestostudio.restaurant_pos.repository.MenuCategoryRepository;
import com.crestostudio.restaurant_pos.repository.MenuItemRepository;
import com.crestostudio.restaurant_pos.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final SecurityUtils securityUtils;

    public MenuCategoryService(
            MenuCategoryRepository menuCategoryRepository,
            MenuItemRepository menuItemRepository,
            SecurityUtils securityUtils) {
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public Page<MenuCategoryResponse> getAllCategories(Pageable pageable) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        return menuCategoryRepository.findByRestaurantId(restaurant.getId(), pageable)
                .map(MenuCategoryResponse::from);
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> getAllCategoriesList() {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        return menuCategoryRepository.findByRestaurantId(restaurant.getId()).stream()
                .map(MenuCategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuCategoryResponse getCategoryById(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();
        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return MenuCategoryResponse.from(category);
    }

    @Transactional
    public MenuCategoryResponse createCategory(MenuCategoryRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        if (menuCategoryRepository.existsByRestaurantIdAndNameIgnoreCase(restaurant.getId(), request.name().trim())) {
            throw new ResourceAlreadyExistsException("Category with name '" + request.name() + "' already exists in this restaurant");
        }

        MenuCategory category = new MenuCategory();
        category.setRestaurant(restaurant);
        category.setName(request.name().trim());
        category.setDescription(request.description());

        MenuCategory saved = menuCategoryRepository.save(category);
        return MenuCategoryResponse.from(saved);
    }

    @Transactional
    public MenuCategoryResponse updateCategory(UUID id, MenuCategoryRequest request) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        if (menuCategoryRepository.existsByRestaurantIdAndNameIgnoreCaseAndIdNot(restaurant.getId(), request.name().trim(), id)) {
            throw new ResourceAlreadyExistsException("Category with name '" + request.name() + "' already exists in this restaurant");
        }

        category.setName(request.name().trim());
        category.setDescription(request.description());

        MenuCategory updated = menuCategoryRepository.save(category);
        return MenuCategoryResponse.from(updated);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Restaurant restaurant = securityUtils.getCurrentRestaurant();

        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(id, restaurant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        if (menuItemRepository.existsByCategoryId(id)) {
            throw new ValidationException("Cannot delete menu category. Menu items still exist under this category.");
        }

        menuCategoryRepository.delete(category);
    }
}
