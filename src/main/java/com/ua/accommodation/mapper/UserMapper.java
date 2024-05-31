package com.ua.accommodation.mapper;

import com.ua.accommodation.config.MapperConfig;
import com.ua.accommodation.dto.UserRegistrationRequestDto;
import com.ua.accommodation.dto.UserRegistrationResponseDto;
import com.ua.accommodation.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto user);
}
