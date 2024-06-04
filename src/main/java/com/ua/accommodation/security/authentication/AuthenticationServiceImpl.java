package com.ua.accommodation.security.authentication;

import com.ua.accommodation.dto.auth.UserLoginRequestDto;
import com.ua.accommodation.dto.auth.UserLoginResponseDto;
import com.ua.accommodation.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(), requestDto.getPassword())
        );

        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }
}
