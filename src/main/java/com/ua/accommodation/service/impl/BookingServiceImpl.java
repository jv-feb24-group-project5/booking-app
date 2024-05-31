package com.ua.accommodation.service.impl;

import com.ua.accommodation.controller.service.BookingService;
import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.mapper.BookingMapper;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.repository.BookingRepository;
import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    public BookingResponseDto createBooking(Long userId, BookingRequestDto requestDto) {
        Booking newBooking = bookingMapper.toEntity(requestDto);
        newBooking.setUserId(userId);
        newBooking.setStatus(Booking.Status.PENDING);
        return bookingMapper.toResponseDto(bookingRepository.save(newBooking));
    }

    @Override
    public List<BookingResponseDto> getUsersBookingsByStatus(Long userId, Booking.Status status) {
        return bookingRepository.findByIdAndStatus(userId, status).stream()
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId) {
        return bookingMapper.toResponseDto(getBooking(bookingId));
    }

    @Override
    public BookingResponseDto updateBookingById(Long userId, Long bookingId, BookingUpdateDto updateDto) {
        Booking booking = getBooking(bookingId);
        checkUserOwnershipOfBooking(userId, booking);
        booking.setCheckInDate(updateDto.getCheckInDate());
        booking.setCheckOutDate((updateDto.getCheckOutDate()));
        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBookingById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        checkUserOwnershipOfBooking(userId, booking);
        booking.setStatus(Booking.Status.CANCELED);
        bookingRepository.save(booking);
    }

    private static void checkUserOwnershipOfBooking(Long userId, Booking booking) {
        if (booking.getUserId() != userId) {
            throw new AccessDeniedException(
                    "User does not have permission to manage this booking");
        }
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking with id " + bookingId)
        );
    }
}
