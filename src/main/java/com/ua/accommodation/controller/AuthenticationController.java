package com.ua.accommodation.controller;

import com.ua.accommodation.dto.UserLoginRequestDto;
import com.ua.accommodation.dto.UserLoginResponseDto;
import com.ua.accommodation.dto.UserRegistrationRequestDto;
import com.ua.accommodation.dto.UserRegistrationResponseDto;
import com.ua.accommodation.exception.RegistrationException;
import com.ua.accommodation.security.authentication.AuthenticationService;
import com.ua.accommodation.security.registration.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto requestDto
    ) throws RegistrationException {
        return registrationService.register(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
