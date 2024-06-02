package com.ua.accommodation.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.SessionCollection;
import com.stripe.param.checkout.SessionListParams;
import com.ua.accommodation.dto.PaymentRequestDto;
import com.ua.accommodation.dto.PaymentResponseDto;
import com.ua.accommodation.service.StripeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/sessions")
    @ResponseBody
    public PaymentResponseDto createSession(@RequestBody PaymentRequestDto sessionDto) {
        return stripeService.createPaymentSession(sessionDto);
    }

    @GetMapping("/sessions/{id}")
    @ResponseBody
    public PaymentResponseDto retrieveSession(@PathVariable String id) {
        return stripeService.retrieveSession(id);
    }

    @GetMapping("/sessions")
    @ResponseBody
    public List<Session> getSessions() throws StripeException {
        SessionListParams params = SessionListParams.builder().setLimit(3L).build();
        SessionCollection sessions = Session.list(params);
        return sessions.getData();
    }

    @GetMapping("/sessions/cancel")
    @ResponseBody
    public PaymentResponseDto handleCancel(@RequestParam("session_id") String sessionId) {
        return stripeService.retrieveSession(sessionId);
    }

    @GetMapping("/sessions/success")
    @ResponseBody
    public PaymentResponseDto handleSuccess(@RequestParam("session_id") String sessionId) {
        return stripeService.retrieveSession(sessionId);
    }
}
