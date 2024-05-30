package com.ua.accommodation.model;

import java.math.BigDecimal;
import java.net.URL;

public class Payment {
    private Long id;
    private Status status;
    private Long bookingId;
    private URL SessionURL;
    private String SessionId;
    private BigDecimal AmountToPay;

    public enum Status {
        PENDING,
        PAID
    }
}
