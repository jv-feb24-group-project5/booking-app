package com.ua.accommodation.dto.payment;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class PaymentRequestDto {

    private String bookingId;
    private String userEmail;
    private String userName;
    private String message;
    private BigDecimal amount;
    private Map<String, String> data;
}
