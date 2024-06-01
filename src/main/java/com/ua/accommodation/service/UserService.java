package com.ua.accommodation.service;

import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.dto.user.UserUpdateProfileDto;
import com.ua.accommodation.dto.user.UserUpdateRoleDto;

public interface UserService {
    UserResponseDto getUser(Long userId);

    UserResponseDto updateRoles(Long userId, UserUpdateRoleDto updateRoleDto);

    UserResponseDto updateProfile(Long userId, UserUpdateProfileDto updateProfileDto);
}
