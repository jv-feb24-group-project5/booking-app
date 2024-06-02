package com.ua.accommodation.mapper;

import com.ua.accommodation.config.MapperConfig;
import com.ua.accommodation.dto.UserRegistrationRequestDto;
import com.ua.accommodation.dto.UserRegistrationResponseDto;
import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.model.Role;
import com.ua.accommodation.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    UserRegistrationResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto user);

    UserResponseDto toResponseDto(User user);

    default Set<Role.RoleName> map(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void setRoles(@MappingTarget UserResponseDto userResponseDto, User user) {
        userResponseDto.setRoles(map(user.getRoles()));
    }
}
