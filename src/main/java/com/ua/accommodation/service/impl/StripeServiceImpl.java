package com.ua.accommodation.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.ua.accommodation.dto.payment.PaymentRequestDto;
import com.ua.accommodation.dto.payment.PaymentResponseDto;
import com.ua.accommodation.mapper.PaymentMapper;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.Payment;
import com.ua.accommodation.model.User;
import com.ua.accommodation.repository.PaymentRepository;
import com.ua.accommodation.service.AccommodationService;
import com.ua.accommodation.service.BookingService;
import com.ua.accommodation.service.StripeService;
import com.ua.accommodation.service.event.NotificationEvent;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    private static final String SESSION_ID = "session_id={CHECKOUT_SESSION_ID}";
    private static final String CLIENT_URL = "/api/checkout/payments/";
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingService bookingService;
    private final ApplicationEventPublisher eventPublisher;
    private final AccommodationService accommodationService;
    @Value("${stripe.secret.key}")
    private String stripeApiKey;
    @Value("${stripe.redirect.url}")
    private String stripeRedirectUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentResponseDto createPaymentSession(
            PaymentRequestDto sessionDto, Authentication authentication
    ) {
        var booking = bookingService.getBooking(sessionDto.getBookingId());
        var accommodation = accommodationService.getAccommodation(booking.getAccommodationID());
        var duration = java.time.temporal.ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        var amountTopay = accommodation.dailyRate().multiply(BigDecimal.valueOf(duration));
        User user = (User) authentication.getPrincipal();
        try {
            Customer customer = findOrCreateCustomer(
                    user.getEmail(), user.getFirstName());
            SessionCreateParams.Builder sessionCreateParamsBuilder = getParamsBuilder(
                    sessionDto, customer, amountTopay);

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
        PaymentResponseDto responseDto = paymentMapper.toDto(payment);
        if (session.getPaymentStatus().equals("paid")) {
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);
            responseDto = paymentMapper.toDto(payment);
            Booking booking = bookingService.getBooking(payment.getBookingId());
            booking.setStatus(Booking.Status.CONFIRMED);
            bookingService.saveBooking(booking);
            responseDto.setMessage("Payment successful.");
            return responseDto;
        }
        responseDto.setMessage("Payment paused, you can complete it later.");
        return responseDto;
    }

    @Scheduled(cron = "0 0 */1 * * *", zone = "Europe/Kiev")
    public void checkExpiredSessions() {
        List<Payment> expiredPayments =
                paymentRepository.findPaymentsByExpiresAtBefore(LocalDateTime.now());
        expiredPayments.forEach(payment -> payment.setStatus(Payment.Status.EXPIRED));
        paymentRepository.saveAll(expiredPayments);
        expiredPayments.forEach(this::publishEvent);
    }

    public List<PaymentResponseDto> findPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findPaymentsByUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public SessionCreateParams.Builder getParamsBuilder(
            PaymentRequestDto sessionDto, Customer customer,
            BigDecimal amountTopay) {
        SessionCreateParams.Builder sessionCreateParamsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomer(customer.getId())
                .setSuccessUrl(stripeRedirectUrl + CLIENT_URL
                        + "success?" + SESSION_ID)
                .setCancelUrl(stripeRedirectUrl + CLIENT_URL
                        + "cancel?" + SESSION_ID);
        sessionCreateParamsBuilder
                .putMetadata("bookingId", String.valueOf(sessionDto.getBookingId()));
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
                                .setUnitAmountDecimal(amountTopay
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
        String message = "Payment update!"
                + System.lineSeparator()
                + "Id: " + payment.getId()
                + System.lineSeparator()
                + "Booking id: " + payment.getBookingId()
                + System.lineSeparator()
                + "Amount to pay: " + payment.getAmountToPay()
                + System.lineSeparator()
                + "Created at: " + payment.getCreated()
                + System.lineSeparator()
                + "Expires at: " + payment.getExpiresAt()
                + System.lineSeparator()
                + "Status: " + payment.getStatus();

        NotificationEvent event = new NotificationEvent(this, message);
        eventPublisher.publishEvent(event);
    }
}
