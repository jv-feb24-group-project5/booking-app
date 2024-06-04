package com.ua.accommodation.controller;

import com.ua.accommodation.dto.auth.UserLoginRequestDto;
import com.ua.accommodation.dto.auth.UserLoginResponseDto;
import com.ua.accommodation.dto.auth.UserRegistrationRequestDto;
import com.ua.accommodation.dto.auth.UserRegistrationResponseDto;
import com.ua.accommodation.exception.RegistrationException;
import com.ua.accommodation.security.authentication.AuthenticationService;
import com.ua.accommodation.security.registration.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "User authentication", description = "Endpoint for managing authentication")
public class AuthenticationController {
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Register new user",
            description = "Endpoint for register new user")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto requestDto
    ) throws RegistrationException {
        return registrationService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user",
            description = "Endpoint for login user")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
