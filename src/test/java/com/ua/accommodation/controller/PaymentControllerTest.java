package com.ua.accommodation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.accommodation.dto.payment.PaymentRequestDto;
import com.ua.accommodation.dto.payment.PaymentResponseDto;
import com.ua.accommodation.model.Payment;
import com.ua.accommodation.service.StripeService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {
    public static final String SESSION_ID = "testSessionId";
    public static final String MESSAGE = "Test message";
    public static final String TEST_URL = "Test url";
    public static final String SECRET = "Secret";
    private static MockMvc mockMvc;

    @MockBean
    private StripeService stripeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "testUser")
    void createSession_ValidRequestDto_CreatesNewPaymentSession() throws Exception {
        PaymentRequestDto requestDto = createPaymentRequest();
        PaymentResponseDto responseDto = createPaymentResponse(requestDto);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testUser",
                        null, Collections.emptyList());
        given(stripeService.createPaymentSession(any(PaymentRequestDto.class),
                eq(authentication))).willReturn(responseDto);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/checkout/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void handleCancel_ExistingSessionId_SetCancelPaymentStatus() throws Exception {
        String sessionId = SESSION_ID;
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setStatus(Payment.Status.EXPIRED);
        given(stripeService.retrieveSession(sessionId)).willReturn(responseDto);

        mockMvc.perform(get("/checkout/payments/cancel")
                        .param("session_id", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void handleSuccess_ExistingSessionId_SetPaidPaymentStatus() throws Exception {
        String sessionId = SESSION_ID;
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setStatus(Payment.Status.PAID);
        given(stripeService.retrieveSession(sessionId)).willReturn(responseDto);

        mockMvc.perform(get("/checkout/payments/success")
                        .param("session_id", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @WithMockUser(username = "testUser")
    void findPayments_ExistingUserId_ReturnsPageOfAllUserPayments() throws Exception {
        Long userId = 1L;
        List<PaymentResponseDto> paymentsList = Collections.singletonList(new PaymentResponseDto());
        given(stripeService.findPaymentsByUserId(eq(userId),
                any(Pageable.class))).willReturn(paymentsList);

        mockMvc.perform(get("/checkout/payments")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(paymentsList.size())))
                .andExpect(content().json(objectMapper.writeValueAsString(paymentsList)));
    }

    private PaymentRequestDto createPaymentRequest() {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setAmount(BigDecimal.TEN);
        requestDto.setBookingId(1L);
        return requestDto;
    }

    private PaymentResponseDto createPaymentResponse(PaymentRequestDto requestDto) {
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setCreated(LocalDateTime.now());
        responseDto.setMessage(MESSAGE);
        responseDto.setStatus(Payment.Status.PENDING);
        responseDto.setAmountToPay(requestDto.getAmount());
        responseDto.setBookingId(requestDto.getBookingId());
        responseDto.setExpiresAt(responseDto.getCreated().plusHours(12L));
        responseDto.setSessionUrl(TEST_URL);
        responseDto.setClientSecret(SECRET);
        return responseDto;
    }
}
