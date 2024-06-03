package com.ua.accommodation.mapper;

import com.stripe.model.checkout.Session;
import com.ua.accommodation.config.MapperConfig;
import com.ua.accommodation.dto.payment.PaymentResponseDto;
import com.ua.accommodation.model.Payment;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    @Mapping(source = "payment.sessionId", target = "clientSecret")
    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "session", target = "status", qualifiedByName = "setStatus")
    @Mapping(source = "session.id", target = "sessionId")
    @Mapping(source = "session.url", target = "sessionUrl")
    @Mapping(source = "session", target = "amountToPay", qualifiedByName = "setAmount")
    @Mapping(source = "session", target = "bookingId", qualifiedByName = "setBookingId")
    Payment toModel(Session session);

    @Named("setStatus")
    default Payment.Status setStatus(Session session) {
        return Payment.Status.findByCode(session.getPaymentStatus());
    }

    @Named("setAmount")
    default BigDecimal setAmount(Session session) {
        return BigDecimal.valueOf(session.getAmountTotal());
    }

    @Named("setBookingId")
    default Long setBookingId(Session session) {
        return Long.valueOf(session.getMetadata().get("bookingId"));
    }
}
