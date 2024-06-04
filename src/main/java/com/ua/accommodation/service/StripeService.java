package com.ua.accommodation.service;

import com.ua.accommodation.dto.payment.PaymentRequestDto;
import com.ua.accommodation.dto.payment.PaymentResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface StripeService {

    PaymentResponseDto createPaymentSession(
            PaymentRequestDto sessionDto, Authentication authentication);

    PaymentResponseDto retrieveSession(String id);

    List<PaymentResponseDto> findPaymentsByUserId(Long userId, Pageable pageable);
}
