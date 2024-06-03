package com.ua.accommodation.dto;

import com.ua.accommodation.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentResponseDto {
    private Long id;
    private Long bookingId;
    private String clientSecret;
    private String sessionUrl;
    private Payment.Status status;
    private BigDecimal amountToPay;
    private String message;
    private LocalDateTime created;
    private LocalDateTime expiresAt;
}
