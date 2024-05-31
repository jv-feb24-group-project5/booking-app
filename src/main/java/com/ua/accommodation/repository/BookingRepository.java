package com.ua.accommodation.repository;

import com.ua.accommodation.model.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = :status")
    List<Booking> findByIdAndStatus(@Param("userId") Long userId, @Param("status") Booking.Status status);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId")
    List<Booking> findByUserId(Long userId);
}
