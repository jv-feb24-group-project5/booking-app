package com.ua.accommodation.controller;

import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.dto.user.UserUpdateProfileDto;
import com.ua.accommodation.dto.user.UserUpdateRoleDto;
import com.ua.accommodation.model.User;
import com.ua.accommodation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    public UserResponseDto getUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getUser(user.getId());
    }

    @PutMapping("{id}/role")
    public UserResponseDto updateRoles(@PathVariable Long id,
                                       @RequestBody UserUpdateRoleDto updateRoleDto) {
        return userService.updateRoles(id, updateRoleDto);
    }

    @PatchMapping("/me")
    public UserResponseDto updateProfile(Authentication authentication,
                                         @RequestBody UserUpdateProfileDto updateProfileDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfile(user.getId(), updateProfileDto);
    }
}
