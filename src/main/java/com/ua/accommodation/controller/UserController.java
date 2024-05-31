package com.ua.accommodation.controller;

import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.dto.user.UserUpdateProfileDto;
import com.ua.accommodation.dto.user.UserUpdateRoleDto;
import com.ua.accommodation.model.User;
import com.ua.accommodation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponseDto getUser(@AuthenticationPrincipal User user) {
        return userService.getUser(user.getId());
    }

    @PutMapping("{id}/role")
    public UserResponseDto updateRoles(@PathVariable Long id,
                                       @RequestBody @Valid UserUpdateRoleDto updateRoleDto) {
        return userService.updateRoles(id, updateRoleDto);
    }

    @PatchMapping("/me")
    public UserResponseDto updateProfile(@AuthenticationPrincipal User user,
                                         @RequestBody
                                         @Valid UserUpdateProfileDto updateProfileDto) {
        return userService.updateProfile(user.getId(), updateProfileDto);
    }
}
