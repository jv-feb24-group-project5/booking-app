package com.ua.accommodation.security.authentication;

import com.ua.accommodation.dto.UserLoginRequestDto;
import com.ua.accommodation.dto.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
