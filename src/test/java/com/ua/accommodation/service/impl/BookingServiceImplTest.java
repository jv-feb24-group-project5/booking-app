package com.ua.accommodation.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.exception.AccommodationUnavailableException;
import com.ua.accommodation.mapper.BookingMapper;
import com.ua.accommodation.model.Booking;
import com.ua.accommodation.model.Booking.Status;
import com.ua.accommodation.model.Role;
import com.ua.accommodation.model.Role.RoleName;
import com.ua.accommodation.repository.BookingRepository;
import com.ua.accommodation.service.event.NotificationEvent;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookingServiceImpl bookingService;
    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final Long accommodationId = 1L;
    private final LocalDate checkInDate = LocalDate.now().plusDays(1);
    private final LocalDate checkOutDate = LocalDate.now().plusDays(5);
    private final Role role = createRole();

    private Role createRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);
        role.setDeleted(false);
        return role;
    }

    private BookingRequestDto createBookingRequestDto() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setAccommodationID(accommodationId);
        requestDto.setCheckInDate(checkInDate);
        requestDto.setCheckOutDate(checkOutDate);
        return requestDto;
    }

    private Booking createBooking() {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setStatus(Status.PENDING);
        return booking;
    }

    private Booking createBookingWithId(Long id) {
        Booking booking = new Booking();
        booking.setId(id);
        return booking;
    }

    private BookingResponseDto createBookingResponseDto() {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(bookingId);
        responseDto.setAccommodationID(accommodationId);
        responseDto.setCheckInDate(checkInDate);
        responseDto.setCheckOutDate(checkOutDate);
        responseDto.setUserId(userId);
        responseDto.setStatus(Status.PENDING);
        return responseDto;
    }

    @Test
    @DisplayName("Create Booking when accommodation is available")
    void createBooking_AccommodationAvailable_BookingCreated() {
        BookingRequestDto requestDto = createBookingRequestDto();
        Booking booking = createBooking();
        BookingResponseDto responseDto = createBookingResponseDto();

        when(bookingMapper.toEntity(requestDto)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        BookingResponseDto actual = bookingService.createBooking(userId, requestDto);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
        assertEquals(actual, responseDto);
    }

    @Test
    @DisplayName("When accommodation s unavailable createBooking throws exception")
    void createBooking_AccommodationUnavailable_ThrowsException() {
        BookingRequestDto requestDto = createBookingRequestDto();

        when(bookingRepository.countConflictingBookings(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(1L);

        assertThrows(AccommodationUnavailableException.class, () -> {
            bookingService.createBooking(userId, requestDto);
        });
    }

    @Test
    @DisplayName("Get Booking by ID when booking exists")
    void getBookingById_BookingExists_ReturnsBooking() {
        Booking booking = createBooking();
        booking.setId(bookingId);

        BookingResponseDto responseDto = createBookingResponseDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        Set<Role> roles = Set.of(role);
        BookingResponseDto actual = bookingService.getBookingById(userId, roles, bookingId);

        verify(bookingRepository, times(1)).findById(bookingId);
        assertEquals(actual, responseDto);
    }

    @Test
    @DisplayName("Get Booking by ID when booking does not exist throws exception")
    void getBookingById_BookingDoesNotExist_ThrowsException() {
        Set<Role> roles = Set.of(role);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookingService.getBookingById(userId, roles, bookingId);
        });
    }

    @Test
    @DisplayName("Update Booking by ID when user is not owner throws exception")
    void updateBookingById_UserIsNotOwner_ThrowsException() {
        Booking booking = createBooking();
        booking.setId(bookingId);
        booking.setUserId(2L);

        BookingUpdateDto updateDto = new BookingUpdateDto();
        updateDto.setCheckInDate(checkInDate);
        updateDto.setCheckOutDate(checkOutDate);

        Set<Role> roles = Set.of(role);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> {
            bookingService.updateBookingById(userId, roles, bookingId, updateDto);
        });
    }

    @Test
    @DisplayName("Update Booking by ID when accommodation is available updates booking")
    void updateBookingById_AccommodationAvailable_UpdatesBooking() {
        Booking booking = createBooking();
        booking.setId(bookingId);
        BookingUpdateDto updateDto = new BookingUpdateDto();
        updateDto.setCheckInDate(checkInDate);
        updateDto.setCheckOutDate(checkOutDate);

        Booking updatedBooking = createBooking();
        updatedBooking.setId(bookingId);
        updatedBooking.setCheckInDate(updateDto.getCheckInDate());
        updatedBooking.setCheckOutDate(updateDto.getCheckOutDate());

        BookingResponseDto responseDto = createBookingResponseDto();
        responseDto.setCheckInDate(updateDto.getCheckInDate());
        responseDto.setCheckOutDate(updateDto.getCheckOutDate());

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
        when(bookingMapper.toResponseDto(updatedBooking)).thenReturn(responseDto);

        Set<Role> roles = Set.of(role);
        BookingResponseDto actual = bookingService.updateBookingById(
                userId, roles, bookingId, updateDto);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
        assertEquals(actual, responseDto);
    }

    @Test
    @DisplayName("Delete Booking by ID when booking exists sets status to CANCELED")
    void deleteBookingById_BookingExists_SetsStatusToCanceled() {
        Booking booking = createBooking();
        booking.setId(bookingId);

        Booking canceledBooking = createBooking();
        canceledBooking.setId(bookingId);
        canceledBooking.setStatus(Status.CANCELED);

        BookingResponseDto responseDto = createBookingResponseDto();
        responseDto.setStatus(Status.CANCELED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(canceledBooking);
        when(bookingMapper.toResponseDto(canceledBooking)).thenReturn(responseDto);

        Set<Role> roles = Set.of(role);
        BookingResponseDto actual = bookingService.deleteBookingById(userId, roles, bookingId);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
        assertEquals(actual, responseDto);
    }

    @Test
    @DisplayName("Delete Booking by ID when booking does not exist throws exception")
    void deleteBookingById_BookingDoesNotExist_ThrowsException() {
        Set<Role> roles = Set.of(role);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookingService.deleteBookingById(userId, roles, bookingId);
        });
    }

    @Test
    @DisplayName("Get Users Bookings by Status returns bookings")
    void getUsersBookingsByStatus_ReturnsBookings() {
        Pageable pageable = Pageable.unpaged();
        Status status = Status.PENDING;

        Booking booking = createBooking();
        BookingResponseDto responseDto = createBookingResponseDto();

        when(bookingRepository.findByIdAndStatus(pageable, userId, status))
                .thenReturn(
                        new PageImpl<>(
                                List.of(booking),
                                PageRequest.of(0, 10),
                                List.of(booking).size()));
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> actual = bookingService
                .getUsersBookingsByStatus(pageable, userId, status);

        verify(bookingRepository, times(1)).findByIdAndStatus(pageable, userId, status);
        assertEquals(List.of(responseDto), actual);
    }

    @Test
    @DisplayName("Get Bookings by User ID returns bookings")
    void getBookingsByUserId_ReturnsBookings() {
        Pageable pageable = Pageable.unpaged();

        Booking booking = createBooking();
        BookingResponseDto responseDto = createBookingResponseDto();

        when(bookingRepository.findByUserId(pageable, userId))
                .thenReturn(new PageImpl<>(
                        List.of(booking),
                        PageRequest.of(0, 10),
                        List.of(booking).size()));
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> actual = bookingService.getBookingsByUserId(pageable, userId);

        verify(bookingRepository, times(1)).findByUserId(pageable, userId);
        assertEquals(List.of(responseDto), actual);
    }

    @Test
    @DisplayName("Get Booking ID as Message returns correct format")
    void getBookingIdAsMessage_ReturnsCorrectFormat() throws Exception {
        Method method = BookingServiceImpl.class
                .getDeclaredMethod("getBookingIdAsMessage", Long.class);
        method.setAccessible(true);

        Long bookingId = 1L;
        String expected = "Booking ID: 1" + System.lineSeparator();

        String actual = (String) method.invoke(bookingService, bookingId);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get List of Expired Bookings as Message returns correct format")
    void getListOfExpiredBookingsAsMessage_ReturnsCorrectFormat() throws Exception {
        Method method = BookingServiceImpl.class
                .getDeclaredMethod("getListOfExpiredBookingsAsMessage", List.class);
        method.setAccessible(true);

        List<Booking> expiredBookings = List.of(createBookingWithId(1L), createBookingWithId(2L));
        String expected =
                "Booking ID: 1" + System.lineSeparator() + System.lineSeparator()
                        + System.lineSeparator() + "Booking ID: 2" + System.lineSeparator()
                        + "Status: EXPIRED" + System.lineSeparator();

        String actual = (String) method.invoke(bookingService, expiredBookings);

        assertEquals(expected, actual);
    }
}