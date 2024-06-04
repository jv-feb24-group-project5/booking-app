package com.ua.accommodation.controller;

import com.ua.accommodation.dto.payment.PaymentRequestDto;
import com.ua.accommodation.dto.payment.PaymentResponseDto;
import com.ua.accommodation.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment Management System", description = "Operations pertaining to "
        + "payments in the system")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/checkout")
@Tag(name = "Payments management", description = "Endpoints for managing payments")
public class PaymentController {
    private final StripeService stripeService;

    @PostMapping("/payments")
    @ResponseBody

    @Operation(
            summary = "Create session",
            description = "You can create new session. You need to give bookingId, "
                    + "user name and email, amount and Set data")
    public PaymentResponseDto createSession(
            @Valid @RequestBody PaymentRequestDto requestDto,
            Authentication authentication
    ) {
        return stripeService.createPaymentSession(requestDto, authentication);
    }

    @GetMapping("/payments/{id}")
    @ResponseBody
    @Operation(
            summary = "Retrieve session",
            description = "You can retrieve session by sessionId. "
                    + "Returns status for this session")
    public PaymentResponseDto retrieveSession(@PathVariable String id) {
        return stripeService.retrieveSession(id);
    }

    @Operation(
            summary = "Handle cansel payment operation.",
            description = "When you press the \"Back\" button you "
                    + "will be redirected to this endpoint."
    )
    @GetMapping("/payments/cancel")
    @ResponseBody
    public PaymentResponseDto handleCancel(@RequestParam("session_id") String sessionId) {
        return stripeService.retrieveSession(sessionId);
    }

    @Operation(
            summary = "Handle success payment operation.",
            description = "When you confirm the payment you will be redirected to this endpoint."
    )
    @GetMapping("/payments/success")
    @ResponseBody
    public PaymentResponseDto handleSuccess(@RequestParam("session_id") String sessionId) {
        return stripeService.retrieveSession(sessionId);
    }

    @Operation(
            summary = "Get all payment operations by users id.",
            description = "Get all payment operations by users id."
    )
    @GetMapping("/payments")
    @ResponseBody
    public List<PaymentResponseDto> findPayments(
            @RequestParam Long userId, Pageable pageable
    ) {
        return stripeService.findPaymentsByUserId(userId, pageable);
    }
}
