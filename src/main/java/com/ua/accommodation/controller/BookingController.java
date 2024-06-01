package com.ua.accommodation.controller;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.User;
import com.ua.accommodation.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController { //todo add Pageable
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BookingResponseDto createBooking(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.createBooking(user.getId(), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponseDto> getUsersBookingsByStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam Booking.Status status) {
        return bookingService.getUsersBookingsByStatus(userId, status);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<BookingResponseDto> getUsersBookings(@AuthenticationPrincipal User user) {
        return bookingService.getBookingsByUserId(user.getId());
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BookingResponseDto getBookingById(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {
        return bookingService.getBookingById(
                user.getId(),
                user.getRoles(),
                bookingId);
    }

    @PatchMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BookingResponseDto updateBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId,
            @RequestBody @Valid BookingUpdateDto updateDtoDto) {
        return bookingService.updateBookingById(
                user.getId(),
                user.getRoles(),
                bookingId,
                updateDtoDto);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BookingResponseDto deleteBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {
        return bookingService.deleteBookingById(
                user.getId(),
                user.getRoles(),
                bookingId);
    }
}
