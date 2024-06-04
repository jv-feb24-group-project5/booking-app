package com.ua.accommodation.dto.payment;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @Min(1)
    private Long bookingId;
    @Min(1)
    private BigDecimal amount;
}
