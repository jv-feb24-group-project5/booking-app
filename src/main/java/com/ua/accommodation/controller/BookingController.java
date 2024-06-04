package com.ua.accommodation.controller;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.model.Booking.Status;
import com.ua.accommodation.model.User;
import com.ua.accommodation.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Bookings management", description = "Endpoints for managing bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Create new booking",
            description = "You can create new booking. Available for admins and users"
    )
    public BookingResponseDto createBooking(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.createBooking(user.getId(), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get bookings by status",
            description = "Get list of bookings based on status"
    )
    public List<BookingResponseDto> getUsersBookingsByStatus(
            Pageable pageable,
            @RequestParam(name = "user_id") Long userId,
            @RequestParam Status status) {
        return bookingService.getUsersBookingsByStatus(pageable, userId, status);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get bookings",
            description = "Get list of bookings for current user"
    )
    public List<BookingResponseDto> getUsersBookings(
            Pageable pageable,
            @AuthenticationPrincipal User user) {
        return bookingService.getBookingsByUserId(pageable, user.getId());
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get booking by id",
            description = "Get specific booking by id"
    )
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
    @Operation(
            summary = "Update booking",
            description = "Update specific booking by booking id. "
                    + "You can update checkInDate and CheckOutDate"
    )
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
    @Operation(
            summary = "Delete booking",
            description = "Delete specific booking by bookingId"
    )
    public BookingResponseDto deleteBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {
        return bookingService.deleteBookingById(
                user.getId(),
                user.getRoles(),
                bookingId);
    }
}
