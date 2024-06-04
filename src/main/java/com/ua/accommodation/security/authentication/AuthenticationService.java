package com.ua.accommodation.security.authentication;

import com.ua.accommodation.dto.auth.UserLoginRequestDto;
import com.ua.accommodation.dto.auth.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
