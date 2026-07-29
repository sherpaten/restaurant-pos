package com.crestostudio.restaurant_pos.controller;

import com.crestostudio.restaurant_pos.dto.request.MenuAvailabilityRequest;
import com.crestostudio.restaurant_pos.dto.request.MenuItemRequest;
import com.crestostudio.restaurant_pos.dto.response.ApiResponse;
import com.crestostudio.restaurant_pos.dto.response.MenuItemResponse;
import com.crestostudio.restaurant_pos.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/menu/items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'WAITER', 'CASHIER', 'COOK')")
    public ResponseEntity<ApiResponse<Page<MenuItemResponse>>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<MenuItemResponse> items = menuItemService.getAllItems(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success("Menu items retrieved successfully", items));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'WAITER', 'CASHIER', 'COOK')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getItemById(@PathVariable UUID id) {
        MenuItemResponse item = menuItemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Menu item retrieved successfully", item));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'WAITER', 'CASHIER', 'COOK')")
    public ResponseEntity<ApiResponse<Page<MenuItemResponse>>> getItemsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<MenuItemResponse> items = menuItemService.getItemsByCategory(categoryId, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success("Menu items retrieved by category successfully", items));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'WAITER', 'CASHIER', 'COOK')")
    public ResponseEntity<ApiResponse<Page<MenuItemResponse>>> searchItems(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<MenuItemResponse> items = menuItemService.searchItems(keyword, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success("Menu items search completed successfully", items));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createItem(
            @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse created = menuItemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Menu item created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse updated = menuItemService.updateItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Menu item updated successfully", updated));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateAvailability(
            @PathVariable UUID id,
            @Valid @RequestBody MenuAvailabilityRequest request) {
        MenuItemResponse updated = menuItemService.updateAvailability(id, request);
        return ResponseEntity.ok(ApiResponse.success("Menu item availability updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable UUID id) {
        boolean deleted = menuItemService.deleteItem(id);
        String msg = deleted ? "Menu item deleted successfully" : "Menu item marked as unavailable because historical orders reference it";
        return ResponseEntity.ok(ApiResponse.success(msg));
    }
}
