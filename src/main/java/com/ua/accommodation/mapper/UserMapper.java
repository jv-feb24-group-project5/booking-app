package com.ua.accommodation.mapper;

import com.ua.accommodation.config.MapperConfig;
import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toResponseDto(User user);
}
