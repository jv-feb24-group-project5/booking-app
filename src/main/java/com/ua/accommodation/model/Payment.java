package com.ua.accommodation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted=false")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private Long bookingId;
    @Column(name = "session_url", nullable = false)
    private String sessionUrl;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private BigDecimal amountToPay;
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private boolean isDeleted;

    public enum Status {
        PENDING("unpaid"),
        PAID("paid"), CANCELED("canceled");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public static Status findByCode(String label) {
            for (Status status : values()) {
                if (status.label.equals(label)) {
                    return status;
                }
            }
            throw new UnsupportedOperationException("Wrong operation code: " + label);
        }
    }

    public void setAmountToPay(BigDecimal amount) {
        this.amountToPay = amount.divide(BigDecimal.valueOf(100L));
    }

    public void setCreated(Long createdTimestamp) {
        this.created = LocalDateTime
                .ofInstant(Instant.ofEpochSecond(createdTimestamp), ZoneId.systemDefault());
    }

    public void setExpiresAt(Long expiresAtTimestamp) {
        this.expiresAt = LocalDateTime
                .ofInstant(Instant.ofEpochSecond(expiresAtTimestamp), ZoneId.systemDefault());
    }
}
