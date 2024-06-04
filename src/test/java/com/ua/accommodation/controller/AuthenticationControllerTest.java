package com.ua.accommodation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.accommodation.dto.UserLoginRequestDto;
import com.ua.accommodation.dto.UserLoginResponseDto;
import com.ua.accommodation.dto.UserRegistrationRequestDto;
import com.ua.accommodation.dto.UserRegistrationResponseDto;
import com.ua.accommodation.security.authentication.AuthenticationService;
import com.ua.accommodation.security.registration.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    private static final String EMAIL = "emailAuth@example.com";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final Long TWO_ID = 2L;
    private static final String PASSWORD = "password";
    private static final String URL_REGISTER = "/auth/registration";
    private static final String URL_LOGIN = "/auth/login";
    private static final String ADMIN = "user@example.com";
    private static final String PASSWORD_ADMIN = "12345678";
    private static final String JWT_TOKEN = "dummyToken";
    private static final String ID = "id";
    private static MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Register user")
    void register_Valid_ReturnUserResponseDtoWithRole() throws Exception {
        final UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setRepeatPassword(PASSWORD)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);
        final UserRegistrationResponseDto expectedResponse = new UserRegistrationResponseDto()
                .setId(TWO_ID)
                .setEmail(EMAIL)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);

        when(registrationService.register(any(UserRegistrationRequestDto.class)))
                .thenReturn(expectedResponse);

        final String jsonRequest = objectMapper.writeValueAsString(requestDto);
        final MvcResult result = mockMvc.perform(post(URL_REGISTER)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        final UserRegistrationResponseDto actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), UserRegistrationResponseDto.class);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields(ID)
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("Login user")
    public void login_Valid_ReturnUserLoginResponseDto()
            throws Exception {
        UserLoginRequestDto loginRequest = new UserLoginRequestDto()
                .setEmail(ADMIN)
                .setPassword(PASSWORD_ADMIN);
        UserLoginResponseDto loginResponse = new UserLoginResponseDto(JWT_TOKEN);

        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenReturn(loginResponse);

        MvcResult result = mockMvc.perform(post(URL_LOGIN)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        UserLoginResponseDto actualResponse =
                objectMapper.readValue(content, UserLoginResponseDto.class);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getToken()).isEqualTo(JWT_TOKEN);
    }
}
