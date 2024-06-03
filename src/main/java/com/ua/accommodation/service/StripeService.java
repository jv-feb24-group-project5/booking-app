package com.ua.accommodation.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.ua.accommodation.dto.PaymentRequestDto;
import com.ua.accommodation.dto.PaymentResponseDto;
import com.ua.accommodation.mapper.PaymentMapper;
import com.ua.accommodation.model.Payment;
import com.ua.accommodation.repository.PaymentRepository;
import com.ua.accommodation.service.event.NotificationEvent;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {
    private static final String SESSION_ID = "session_id={CHECKOUT_SESSION_ID}";
    private static final String CLIENT_URL = "http://localhost:8080/api/checkout/payments/";
    @Value("${stripe.secret.key}")
    private String stripeApiKey;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentResponseDto createPaymentSession(PaymentRequestDto sessionDto) {
        try {
            Customer customer = findOrCreateCustomer(
                    sessionDto.getUserEmail(), sessionDto.getUserName());
            SessionCreateParams.Builder sessionCreateParamsBuilder = getParamsBuilder(
                    sessionDto, customer);

            SessionCreateParams.PaymentIntentData paymentIntentData =
                    SessionCreateParams.PaymentIntentData.builder()
                            .build();
            sessionCreateParamsBuilder.setPaymentIntentData(paymentIntentData);
            Session session = Session.create(sessionCreateParamsBuilder.build());
            Payment payment = paymentMapper.toModel(session);
            paymentRepository.save(payment);
            PaymentResponseDto responseDto = paymentMapper.toDto(payment);
            responseDto.setMessage(" Awaiting payment. ");
            publishEvent(payment);
            return responseDto;
        } catch (StripeException e) {
            log.error("Exception createPaymentSession", e);
        }
        return null;
    }

    public PaymentResponseDto retrieveSession(String id) {
        Session session = null;
        try {
            session = Session.retrieve(id);
        } catch (StripeException e) {
            log.error("Exception retrievePaymentSession", e);
        }
        Payment payment = paymentRepository.findBySessionId(id).orElseThrow(
                () -> new RuntimeException("Some troubles with payment.")
        );
        if (session.getPaymentStatus().equals("paid")) {
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);
            PaymentResponseDto responseDto = paymentMapper.toDto(payment);
            responseDto.setMessage("Payment successful.");
            publishEvent(payment);
            return responseDto;
        }
        if (session.getPaymentStatus().equals("unpaid")) {
            PaymentResponseDto responseDto = paymentMapper.toDto(payment);
            responseDto.setMessage("Payment paused, you can complete it later.");
            publishEvent(payment);
            return responseDto;
        }
        return null;
    }

    public List<PaymentResponseDto> findPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findAll(pageable).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private static SessionCreateParams.Builder getParamsBuilder(
            PaymentRequestDto sessionDto, Customer customer
    ) {
        SessionCreateParams.Builder sessionCreateParamsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomer(customer.getId())
                .setSuccessUrl(CLIENT_URL
                        + "success?" + SESSION_ID)
                .setCancelUrl(CLIENT_URL
                        + "cancel?" + SESSION_ID);
        sessionCreateParamsBuilder.putMetadata("bookingId", sessionDto.getBookingId());
        sessionCreateParamsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData
                                .builder()
                                .setProductData(SessionCreateParams.LineItem.PriceData
                                        .ProductData.builder()
                                        .setName("UA.ACCOMMODATION")
                                        .build()
                                )
                                .setCurrency("USD")
                                .setUnitAmountDecimal(sessionDto.getAmount()
                                        .multiply(BigDecimal.valueOf(100L))
                                )
                                .build())
                        .build()
        ).build();
        return sessionCreateParamsBuilder;
    }

    private Customer findOrCreateCustomer(String email, String fullName) throws StripeException {
        CustomerSearchParams params =
                CustomerSearchParams.builder()
                        .setQuery("email:'" + email + "'")
                        .build();
        CustomerSearchResult search = Customer.search(params);
        Customer customer;
        if (search.getData().isEmpty()) {
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(fullName)
                    .setEmail(email)
                    .build();
            customer = Customer.create(customerCreateParams);
        } else {
            customer = search.getData().getFirst();
        }
        return customer;
    }

    private void publishEvent(Payment payment) {
        StringBuilder builder = new StringBuilder();
        String message = builder.append("Payment update!")
                .append(System.lineSeparator())
                .append("Id: ").append(payment.getId())
                .append(System.lineSeparator())
                .append("Booking id: ").append(payment.getBookingId())
                .append(System.lineSeparator())
                .append("Amount to pay: ").append(payment.getAmountToPay())
                .append(System.lineSeparator())
                .append("Created at: ").append(payment.getCreated())
                .append(System.lineSeparator())
                .append("Expires at: ").append(payment.getExpiresAt())
                .toString();

        NotificationEvent event = new NotificationEvent(this, message);
        eventPublisher.publishEvent(event);
    }
}
