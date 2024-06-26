package com.ua.accommodation.service;

import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.dto.user.UserUpdateEmailDto;
import com.ua.accommodation.dto.user.UserUpdateProfileDto;
import com.ua.accommodation.dto.user.UserUpdateRoleDto;

public interface UserService {
    UserResponseDto getUser(String email);

    UserResponseDto updateRoles(Long userId, UserUpdateRoleDto updateRoleDto);

    UserResponseDto updateProfile(Long userId, UserUpdateProfileDto updateProfileDto);

    UserResponseDto updateEmail(String email, UserUpdateEmailDto updateDto);
}
