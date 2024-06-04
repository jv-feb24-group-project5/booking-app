package com.ua.accommodation.repository;

import com.ua.accommodation.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String id);

    @Query("SELECT p "
            + "FROM Payment p JOIN Booking b ON p.bookingId = b.id WHERE b.userId = :userId")
    List<Payment> findPaymentsByUserId(Long userId);
}
