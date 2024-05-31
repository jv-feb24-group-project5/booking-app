package com.ua.accommodation.controller;

import com.ua.accommodation.controller.service.BookingService;
import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.User;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/bookings")
public class BookingController { //todo add Pageable
    private final BookingService bookingService;

//    @PostMapping
//    public BookingResponseDto createBooking(
//            @AuthenticationPrincipal User user,
//            @RequestBody @Valid BookingRequestDto requestDto) {//todo fix kostili
//        return bookingService.createBooking(user.getId(), requestDto);
//    }

    @GetMapping
    public List<BookingResponseDto> getUsersBookingsByStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam Booking.Status status) {
        return bookingService.getUsersBookingsByStatus(userId, status);
    }

//    @GetMapping("/my")
//    public List<BookingResponseDto> getUsersBookings(@AuthenticationPrincipal User user) {
//        return bookingService.getBookingsByUserId(user.getId());
//    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getUsersBookings(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId,
            @RequestBody BookingUpdateDto updateDtoDto) {
        return bookingService.updateBookingById(user.getId(), bookingId, updateDtoDto);
    }

    @DeleteMapping("/{bookingId}")
    public void updateBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {
        return bookingService.deleteBookingById(user.getId(), bookingId);
    }
}
