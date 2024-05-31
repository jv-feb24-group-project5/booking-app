package com.ua.accommodation.security.registration;

import com.ua.accommodation.dto.UserRegistrationRequestDto;
import com.ua.accommodation.dto.UserRegistrationResponseDto;

public interface RegistrationService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);
}
