package com.ua.accommodation.controller.service;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.model.Booking;
import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long userId, BookingRequestDto requestDto);

    List<BookingResponseDto> getUsersBookingsByStatus(Long userId, Booking.Status status);

    List<BookingResponseDto> getBookingsByUserId(Long userId);

    BookingResponseDto getBookingById(Long bookingId);

    BookingResponseDto updateBookingById(Long userId, Long bookingId, BookingUpdateDto updateDto);

    void deleteBookingById(Long userId, Long bookingId);
}
