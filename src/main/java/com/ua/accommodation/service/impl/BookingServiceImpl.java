package com.ua.accommodation.service.impl;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.exception.AccommodationUnavailableException;
import com.ua.accommodation.mapper.BookingMapper;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.Booking.Status;
import com.ua.accommodation.model.Role;
import com.ua.accommodation.repository.BookingRepository;
import com.ua.accommodation.service.BookingService;
import com.ua.accommodation.service.event.NotificationEvent;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public BookingResponseDto createBooking(Long userId, BookingRequestDto requestDto) {
        checkAccommodationAvailability(
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                requestDto.getAccommodationID());
        Booking newBooking = bookingMapper.toEntity(requestDto);
        newBooking.setUserId(userId);
        newBooking.setStatus(Status.PENDING);
        Booking savedBooking = bookingRepository.save(newBooking);
        publishEvent(getBookingAsMessage(savedBooking));
        return bookingMapper.toResponseDto(savedBooking);
    }

    @Override
    public List<BookingResponseDto> getUsersBookingsByStatus(
            Pageable pageable,
            Long userId,
            Status status) {
        return bookingRepository.findByIdAndStatus(pageable, userId, status).stream()
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserId(Pageable pageable, Long userId) {
        return bookingRepository.findByUserId(pageable, userId).stream()
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Set<Role> roles, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (isUserNotAdmin(roles)) {
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
        if (isUserNotAdmin(roles)) {
            checkUserOwnershipOfBooking(userId, booking);
        }
        checkAccommodationAvailability(
                updateDto.getCheckInDate(),
                updateDto.getCheckOutDate(),
                booking.getAccommodationID());
        booking.setCheckInDate(updateDto.getCheckInDate());
        booking.setCheckOutDate((updateDto.getCheckOutDate()));
        Booking savedBooking = bookingRepository.save(booking);
        publishEvent(getBookingAsMessage(savedBooking));
        return bookingMapper.toResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto deleteBookingById(Long userId, Set<Role> roles, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (isUserNotAdmin(roles)) {
            checkUserOwnershipOfBooking(userId, booking);
        }
        booking.setStatus(Status.CANCELED);
        Booking savedBooking = bookingRepository.save(booking);
        publishEvent(savedBooking);
        return bookingMapper.toResponseDto(savedBooking);
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking with id " + bookingId)
        );
    }

    private boolean isUserNotAdmin(Set<Role> roles) {
        return roles.stream().noneMatch(r -> r.getName().equals(Role.RoleName.ADMIN));
    }

    private void checkUserOwnershipOfBooking(Long userId, Booking booking) {
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

    @Scheduled(cron = "0 * 0 * * *", zone = "Europe/Kiev")
    private void sendExpiredBookingsNotification() {
        List<Booking> expiredBookings =
                bookingRepository.findBookingByStatusInAndCheckOutDateBefore(
                        Set.of(Status.CONFIRMED, Status.PENDING),
                        LocalDate.now()
                );
        if (expiredBookings.isEmpty()) {
            publishEvent("No expired bookings today");
        } else {
            expiredBookings.forEach(b -> b.setStatus(Status.EXPIRED));
            bookingRepository.saveAll(expiredBookings);
            publishEvent(getListOfExpiredBookingsAsMessage(expiredBookings));
        }
    }
    private void publishEvent(Booking booking) {
        String message = "Booking update!" +
                System.lineSeparator() +
                "Id: " + booking.getId() +
                System.lineSeparator() +
                "User id: " + booking.getUserId() +
                System.lineSeparator() +
                "Accommodation id: " + booking.getAccommodationID() +
                System.lineSeparator() +
                "Status: " + booking.getStatus() +
                System.lineSeparator() +
                "Check in date: " + booking.getCheckInDate() +
                System.lineSeparator() +
                "Check out date: " + booking.getCheckOutDate();

        NotificationEvent event = new NotificationEvent(this, message);
        eventPublisher.publishEvent(event);
    }

    private String getListOfExpiredBookingsAsMessage(List<Booking> expiredBookings) {
        return expiredBookings.stream()
                .map(b -> getBookingIdAsMessage(b.getId()))
                .collect(Collectors.joining(
                        System.lineSeparator() + System.lineSeparator()
                ))
                + "Status: "
                + Status.EXPIRED
                + System.lineSeparator();
    }

    private String getBookingIdAsMessage(Long id) {
        return "Booking ID: "
                + id
                + System.lineSeparator();
    }

    private String getBookingAsMessage(Booking booking) {
        return getBookingIdAsMessage(booking.getId())
                + "User ID: "
                + booking.getUserId()
                + System.lineSeparator()
                + "Accommodation ID: "
                + booking.getAccommodationID()
                + System.lineSeparator()
                + "Check out date: "
                + booking.getCheckOutDate()
                + System.lineSeparator()
                + "Check in date: "
                + booking.getCheckInDate()
                + System.lineSeparator()
                + "Status: "
                + booking.getStatus()
                + System.lineSeparator();
    }
}
