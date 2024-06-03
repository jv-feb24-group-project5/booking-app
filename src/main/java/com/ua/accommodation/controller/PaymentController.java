package com.ua.accommodation.controller;

import com.ua.accommodation.dto.PaymentRequestDto;
import com.ua.accommodation.dto.PaymentResponseDto;
import com.ua.accommodation.service.StripeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/checkout")
public class PaymentController {
    private final StripeService stripeService;

    @PostMapping("/payments")
    @ResponseBody
    public PaymentResponseDto createSession(@RequestBody PaymentRequestDto sessionDto) {
        return stripeService.createPaymentSession(sessionDto);
    }

    @GetMapping("/payments/{id}")
    @ResponseBody
    public PaymentResponseDto retrieveSession(@PathVariable String id) {
        return stripeService.retrieveSession(id);
    }

    @GetMapping("/payments/cancel")
    @ResponseBody
    public PaymentResponseDto handleCancel(@RequestParam("session_id") String sessionId) {
        return stripeService.retrieveSession(sessionId);
    }

    @GetMapping("/payments/success")
    @ResponseBody
    public PaymentResponseDto handleSuccess(@RequestParam("session_id") String sessionId) {
        return stripeService.retrieveSession(sessionId);
    }

    @GetMapping("/payments")
    @ResponseBody
    public List<PaymentResponseDto> findPayments(
            @RequestParam Long userId, Pageable pageable
    ) {
        return stripeService.findPaymentsByUserId(userId, pageable);
    }
}
