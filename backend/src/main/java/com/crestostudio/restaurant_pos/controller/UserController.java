package com.crestostudio.restaurant_pos.controller;

import com.crestostudio.restaurant_pos.dto.request.RoleUpdateRequest;
import com.crestostudio.restaurant_pos.dto.request.UserCreateRequest;
import com.crestostudio.restaurant_pos.dto.request.UserUpdateRequest;
import com.crestostudio.restaurant_pos.dto.response.ApiResponse;
import com.crestostudio.restaurant_pos.dto.response.PasswordResetResponse;
import com.crestostudio.restaurant_pos.dto.response.UserResponse;
import com.crestostudio.restaurant_pos.dto.response.UserSummaryResponse;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;
import com.crestostudio.restaurant_pos.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserResponse>> createEmployee(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<UserSummaryResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<UserSummaryResponse> employees = userService.getAllEmployees(PageRequest.of(page, size, sort), role, status);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<UserResponse>> getEmployeeById(@PathVariable UUID id) {
        UserResponse response = userService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee details retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserResponse>> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserResponse>> activateEmployee(@PathVariable UUID id) {
        UserResponse response = userService.activateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee activated successfully", response));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateEmployee(@PathVariable UUID id) {
        UserResponse response = userService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deactivated successfully", response));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<UserResponse>> changeRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleUpdateRequest request) {
        UserResponse response = userService.changeRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee role updated successfully", response));
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> resetPassword(@PathVariable UUID id) {
        PasswordResetResponse response = userService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success("Employee password reset successfully", response));
    }

    @PatchMapping("/{id}/delete")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> softDeleteEmployee(@PathVariable UUID id) {
        userService.softDeleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee soft deleted successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<UserSummaryResponse>>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<UserSummaryResponse> employees = userService.searchEmployees(keyword, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success("Employee search completed successfully", employees));
    }
}
