package com.ua.accommodation.repository;

import com.ua.accommodation.model.Booking;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = :status")
    Page<Booking> findByIdAndStatus(
            Pageable pageable,
            @Param("userId")
            Long userId,
            @Param("status")
            Booking.Status status);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId")
    Page<Booking> findByUserId(Pageable pageable, Long userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.accommodationID = :accommodationId AND "
            + "(b.checkInDate < :newCheckOutDate AND b.checkOutDate > :newCheckInDate)"
            + "AND (b.status != 'CANCELED' AND b.status != 'EXPIRED')")
    Long countConflictingBookings(
            Long accommodationId,
            LocalDate newCheckInDate,
            LocalDate newCheckOutDate);

    List<Booking> findBookingByStatusInAndCheckOutDateBefore(Collection<Booking.Status> status,
                                                             LocalDate checkOutDate);
}
