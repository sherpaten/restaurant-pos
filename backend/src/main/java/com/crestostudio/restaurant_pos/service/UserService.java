package com.crestostudio.restaurant_pos.service;

import com.crestostudio.restaurant_pos.dto.request.RoleUpdateRequest;
import com.crestostudio.restaurant_pos.dto.request.UserCreateRequest;
import com.crestostudio.restaurant_pos.dto.request.UserUpdateRequest;
import com.crestostudio.restaurant_pos.dto.response.PasswordResetResponse;
import com.crestostudio.restaurant_pos.dto.response.UserResponse;
import com.crestostudio.restaurant_pos.dto.response.UserSummaryResponse;
import com.crestostudio.restaurant_pos.enums.UserRole;
import com.crestostudio.restaurant_pos.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse createEmployee(UserCreateRequest request);

    Page<UserSummaryResponse> getAllEmployees(Pageable pageable, UserRole role, UserStatus status);

    UserResponse getEmployeeById(UUID id);

    UserResponse updateEmployee(UUID id, UserUpdateRequest request);

    UserResponse activateEmployee(UUID id);

    UserResponse deactivateEmployee(UUID id);

    UserResponse changeRole(UUID id, RoleUpdateRequest request);

    PasswordResetResponse resetPassword(UUID id);

    void softDeleteEmployee(UUID id);

    Page<UserSummaryResponse> searchEmployees(String keyword, Pageable pageable);
}
