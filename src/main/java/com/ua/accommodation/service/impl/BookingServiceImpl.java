package com.ua.accommodation.service.impl;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.exception.AccommodationUnavailableException;
import com.ua.accommodation.mapper.BookingMapper;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.Role;
import com.ua.accommodation.repository.BookingRepository;
import com.ua.accommodation.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    public BookingResponseDto createBooking(Long userId, BookingRequestDto requestDto) {
        checkAccommodationAvailability(
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                requestDto.getAccommodationID());
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
    public BookingResponseDto getBookingById(Long userId, Set<Role> roles, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (!isUserAdmin(roles)) {
            checkUserOwnershipOfBooking(userId, booking);
        }
        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public BookingResponseDto updateBookingById(
            Long userId,
            Set<Role> roles,
            Long bookingId,
            BookingUpdateDto updateDto) {
        Booking booking = getBooking(bookingId);
        if (!isUserAdmin(roles)) {
            checkUserOwnershipOfBooking(userId, booking);
        }
        checkAccommodationAvailability(
                updateDto.getCheckInDate(),
                updateDto.getCheckOutDate(),
                booking.getAccommodationID());
        booking.setCheckInDate(updateDto.getCheckInDate());
        booking.setCheckOutDate((updateDto.getCheckOutDate()));
        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto deleteBookingById(Long userId, Set<Role> roles, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (!isUserAdmin(roles)) {
            checkUserOwnershipOfBooking(userId, booking);
        }
        booking.setStatus(Booking.Status.CANCELED);
        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking with id " + bookingId)
        );
    }

    private static boolean isUserAdmin(Set<Role> roles) {
        return roles.stream().anyMatch(r -> r.getName().equals(Role.RoleName.ADMIN));
    }

    private static void checkUserOwnershipOfBooking(Long userId, Booking booking) {
        if (booking.getUserId() != userId) {
            throw new AccessDeniedException(
                    "User does not have permission to manage this booking");
        }
    }

    private void checkAccommodationAvailability(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Long accommodationID) {
        long conflicts = bookingRepository.countConflictingBookings(
                accommodationID, checkInDate, checkOutDate);
        if (conflicts > 0) {
            throw new AccommodationUnavailableException(
                    "Accommodation is not available from the "
                            + checkInDate + " to the " + checkOutDate);
        }
    }
}
