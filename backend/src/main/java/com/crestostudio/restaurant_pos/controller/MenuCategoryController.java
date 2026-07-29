package com.crestostudio.restaurant_pos.controller;

import com.crestostudio.restaurant_pos.dto.request.MenuCategoryRequest;
import com.crestostudio.restaurant_pos.dto.response.ApiResponse;
import com.crestostudio.restaurant_pos.dto.response.MenuCategoryResponse;
import com.crestostudio.restaurant_pos.service.MenuCategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/menu/categories")
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    public MenuCategoryController(MenuCategoryService menuCategoryService) {
        this.menuCategoryService = menuCategoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'WAITER', 'CASHIER', 'COOK')")
    public ResponseEntity<ApiResponse<Page<MenuCategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<MenuCategoryResponse> categories = menuCategoryService.getAllCategories(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success("Menu categories retrieved successfully", categories));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'WAITER', 'CASHIER', 'COOK')")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> getCategoryById(@PathVariable UUID id) {
        MenuCategoryResponse category = menuCategoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Menu category retrieved successfully", category));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> createCategory(
            @Valid @RequestBody MenuCategoryRequest request) {
        MenuCategoryResponse created = menuCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Menu category created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody MenuCategoryRequest request) {
        MenuCategoryResponse updated = menuCategoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Menu category updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        menuCategoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Menu category deleted successfully"));
    }
}
