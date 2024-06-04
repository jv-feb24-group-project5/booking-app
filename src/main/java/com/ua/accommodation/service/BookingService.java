package com.ua.accommodation.service;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.Booking.Status;
import com.ua.accommodation.model.Role;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponseDto createBooking(Long userId, BookingRequestDto requestDto);

    List<BookingResponseDto> getUsersBookingsByStatus(
            Pageable pageable,
            Long userId,
            Status status);

    List<BookingResponseDto> getBookingsByUserId(Pageable pageable, Long userId);

    BookingResponseDto getBookingById(Long userId, Set<Role> roles, Long bookingId);

    BookingResponseDto updateBookingById(
            Long userId, Set<Role> roles,
            Long bookingId,
            BookingUpdateDto updateDto);

    BookingResponseDto deleteBookingById(Long userId, Set<Role> roles, Long bookingId);

    Booking getBooking(Long bookingId);

    Booking saveBooking(Booking booking);
}
