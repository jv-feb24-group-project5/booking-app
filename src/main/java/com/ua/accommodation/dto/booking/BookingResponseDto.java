package com.ua.accommodation.dto.booking;

import com.ua.accommodation.model.Booking;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationID;
    private Long userId;
    private Booking.Status status;
}
