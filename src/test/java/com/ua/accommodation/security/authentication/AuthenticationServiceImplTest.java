package com.ua.accommodation.security.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ua.accommodation.dto.UserLoginRequestDto;
import com.ua.accommodation.dto.UserLoginResponseDto;
import com.ua.accommodation.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    public static final String EMAIL = "user@example.com";
    public static final String PASSWORD = "password";
    public static final String EXPECTED_TOKEN = "mockedToken";
    public static final String BAD_CREDENTIALS = "Bad credentials";
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private UserLoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        loginRequestDto = new UserLoginRequestDto();
        loginRequestDto.setEmail(EMAIL);
        loginRequestDto.setPassword(PASSWORD);
    }

    @DisplayName("Authenticate with valid credentials returns token")
    @Test
    void authenticate_ValidCredentials_ReturnsToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(), loginRequestDto.getPassword());
        String expectedToken = EXPECTED_TOKEN;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(authentication.getName())).thenReturn(expectedToken);

        UserLoginResponseDto response = authenticationService.authenticate(loginRequestDto);
        String actualToken = response.getToken();

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(authentication.getName());
        assertEquals(expectedToken, actualToken);
    }

    @DisplayName("Authenticate with invalid credentials throws AuthenticationException")
    @Test
    void authenticate_InvalidCredentials_ThrowsAuthenticationException() {
        String expected = BAD_CREDENTIALS;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(expected));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                authenticationService.authenticate(loginRequestDto));
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }
}
