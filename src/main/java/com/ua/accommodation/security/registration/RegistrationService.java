package com.ua.accommodation.security.registration;

import com.ua.accommodation.dto.auth.UserRegistrationRequestDto;
import com.ua.accommodation.dto.auth.UserRegistrationResponseDto;

public interface RegistrationService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);
}
