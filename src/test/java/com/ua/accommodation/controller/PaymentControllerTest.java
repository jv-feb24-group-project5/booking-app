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
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {

    private static MockMvc mockMvc;

    @MockBean
    StripeService stripeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void createSession_ValidRequestDto_CreatesNewPaymentSession() throws Exception {
        PaymentRequestDto requestDto = createPaymentRequest();
        PaymentResponseDto responseDto = createPaymentResponse(requestDto);
        given(stripeService.createPaymentSession(any(PaymentRequestDto.class))).willReturn(responseDto);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/checkout/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private PaymentRequestDto createPaymentRequest() {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setAmount(BigDecimal.TEN);
        requestDto.setUserEmail("test@gmail.com");
        requestDto.setBookingId("1");
        requestDto.setMessage("Test Message");
        requestDto.setUserName("Test name");
        requestDto.setData(new HashMap<>());
        return requestDto;
    }

    private PaymentResponseDto createPaymentResponse(PaymentRequestDto requestDto) {
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setCreated(LocalDateTime.now());
        responseDto.setMessage(requestDto.getMessage());
        responseDto.setStatus(Payment.Status.PENDING);
        responseDto.setAmountToPay(requestDto.getAmount());
        responseDto.setBookingId(Long.valueOf(requestDto.getBookingId()));
        responseDto.setExpiresAt(responseDto.getCreated().plusHours(12L));
        responseDto.setSessionUrl("testUrl");
        responseDto.setClientSecret("test secret");
        return responseDto;
    }

    @Test
    public void handleCancel_ExistingSessionId_SetCancelPaymentStatus() throws Exception {
        String sessionId = "testSessionId";
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setStatus(Payment.Status.CANCELED);

        given(stripeService.retrieveSession(sessionId)).willReturn(responseDto);

        mockMvc.perform(get("/checkout/payments/cancel")
                        .param("session_id", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    public void handleSuccess_ExistingSessionId_SetPaidPaymentStatus() throws Exception {
        String sessionId = "testSessionId";
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setStatus(Payment.Status.PAID);

        given(stripeService.retrieveSession(sessionId)).willReturn(responseDto);

        mockMvc.perform(get("/checkout/payments/success")
                        .param("session_id", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    public void findPayments_ExistingUserId_ReturnsPageOfAllUserPayments() throws Exception {
        Long userId = 1L;
        List<PaymentResponseDto> paymentsList = Collections.singletonList(new PaymentResponseDto());

        given(stripeService.findPaymentsByUserId(eq(userId), any(Pageable.class))).willReturn(paymentsList);

        mockMvc.perform(get("/checkout/payments")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(paymentsList.size())))
                .andExpect(content().json(objectMapper.writeValueAsString(paymentsList)));
    }
}
