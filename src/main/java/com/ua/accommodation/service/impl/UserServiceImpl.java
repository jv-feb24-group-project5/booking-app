package com.ua.accommodation.service.impl;

import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.dto.user.UserUpdateProfileDto;
import com.ua.accommodation.dto.user.UserUpdateRoleDto;
import com.ua.accommodation.mapper.UserMapper;
import com.ua.accommodation.model.User;
import com.ua.accommodation.repository.UserRepository;
import com.ua.accommodation.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user with id: " + userId)
        );
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateRoles(Long userId, UserUpdateRoleDto updateRoleDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user with id: " + userId)
        );
        user.setRoles(updateRoleDto.getRoles());
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateProfile(Long userId, UserUpdateProfileDto updateProfileDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user with id: " + userId)
        );
        user.setEmail(updateProfileDto.getEmail());
        user.setFirstName(updateProfileDto.getFirstName());
        user.setLastName(updateProfileDto.getLastName());
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }
}
