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
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripeService {
    private static final String CLIENT_URL = "http://localhost:8080";
    @Value("${stripe.secret.key}")
    private String stripeApiKey;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentResponseDto createPaymentSession(PaymentRequestDto sessionDto) {
        try {
            Customer customer = findOrCreateCustomer(
                    sessionDto.getUserEmail(), sessionDto.getUserName());
            SessionCreateParams.Builder sessionCreateParamsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(CLIENT_URL
                            + "/checkout/sessions/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(CLIENT_URL
                            + "/checkout/sessions/cancel?session_id={CHECKOUT_SESSION_ID}");
            sessionCreateParamsBuilder.putMetadata("bookingId", sessionDto.getBookingId());
            sessionCreateParamsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData
                                    .builder()
                                    .setProductData(SessionCreateParams.LineItem.PriceData
                                            .ProductData.builder()
                                            .putMetadata("booking_id", sessionDto.getBookingId())
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

            SessionCreateParams.PaymentIntentData paymentIntentData =
                    SessionCreateParams.PaymentIntentData.builder()
                            .putMetadata("cart_id", "12312121212121212121212")
                            .putMetadata("user112121212121221212_id", sessionDto.getBookingId())
                            .build();
            sessionCreateParamsBuilder.setPaymentIntentData(paymentIntentData);
            Session session = Session.create(sessionCreateParamsBuilder.build());
            Payment payment = paymentMapper.toModel(session);
            paymentRepository.save(payment);
            PaymentResponseDto responseDto = paymentMapper.toDto(payment);
            responseDto.setMessage(" Awaiting payment. ");
            return responseDto;
        } catch (StripeException e) {
            log.error("Exception createPaymentSession", e);
        }
        return null;
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
            return responseDto;
        }
        if (session.getPaymentStatus().equals("unpaid")) {
            PaymentResponseDto responseDto = paymentMapper.toDto(payment);
            responseDto.setMessage("Payment paused, you can complete it later.");
            return responseDto;
        }
        return null;
    }
}
