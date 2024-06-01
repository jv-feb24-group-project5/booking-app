package com.ua.accommodation.service;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.Role;
import java.util.List;
import java.util.Set;

public interface BookingService {
    BookingResponseDto createBooking(Long userId, BookingRequestDto requestDto);

    List<BookingResponseDto> getUsersBookingsByStatus(Long userId, Booking.Status status);

    List<BookingResponseDto> getBookingsByUserId(Long userId);

    BookingResponseDto getBookingById(Long userId, Set<Role> roles, Long bookingId);

    BookingResponseDto updateBookingById(Long userId, Set<Role> roles, Long bookingId, BookingUpdateDto updateDto);

    void deleteBookingById(Long userId, Set<Role> roles, Long bookingId);
}
