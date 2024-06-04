package com.ua.accommodation.controller;

import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.dto.user.UserUpdateProfileDto;
import com.ua.accommodation.dto.user.UserUpdateRoleDto;
import com.ua.accommodation.model.User;
import com.ua.accommodation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users management", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get information about user",
            description = "Get information about current authenticate user")
    public UserResponseDto getUser(@AuthenticationPrincipal User user) {
        return userService.getUser(user.getEmail());
    }

    @PutMapping("{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update roles",
            description = "Update roles for user by user id. Available only for admins. "
                    + "You need make request with Set roles. Don't forget about id")
    public UserResponseDto updateRoles(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRoleDto updateRoleDto) {
        return userService.updateRoles(id, updateRoleDto);
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Update profile",
            description = "You can update personal info about user. "
                    + "You need give all fields in request. "
                    + "If you don't wanna change something just give old value")
    public UserResponseDto updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserUpdateProfileDto updateProfileDto) {
        return userService.updateProfile(user.getId(), updateProfileDto);
    }
}
