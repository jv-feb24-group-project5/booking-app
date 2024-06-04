package com.ua.accommodation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.core.userdetails.User.withUsername;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.dto.booking.BookingUpdateDto;
import com.ua.accommodation.model.Booking.Status;
import com.ua.accommodation.model.Role;
import com.ua.accommodation.model.User;
import com.ua.accommodation.service.BookingService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER", "ADMIN"})
    void createBooking_ValidRequest_ReturnsCreatedBooking() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRoles(Set.of(new Role(Role.RoleName.USER), new Role(Role.RoleName.ADMIN)));
        setAuthentication(user);

        BookingRequestDto requestDto = createBookingRequestDto();
        BookingResponseDto responseDto = createBookingResponseDto();
        given(bookingService.createBooking(eq(user.getId()), any(BookingRequestDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void getUsersBookingsByStatus_ValidRequest_ReturnsListOfBookings() throws Exception {
        List<BookingResponseDto> bookings = Collections.singletonList(createBookingResponseDto());
        Pageable pageable = PageRequest.of(0, 10);
        given(bookingService.getUsersBookingsByStatus(eq(pageable), eq(1L), eq(Status.CONFIRMED)))
                .willReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .param("user_id", "1")
                        .param("status", "CONFIRMED")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER", "ADMIN"})
    void getUsersBookings_ValidRequest_ReturnsListOfBookings() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRoles(Set.of(new Role(Role.RoleName.USER), new Role(Role.RoleName.ADMIN)));
        setAuthentication(user);

        List<BookingResponseDto> bookings = Collections.singletonList(createBookingResponseDto());
        Pageable pageable = PageRequest.of(0, 10);
        given(bookingService.getBookingsByUserId(eq(pageable), eq(user.getId())))
                .willReturn(bookings);

        mockMvc.perform(get("/bookings/my")
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER", "ADMIN"})
    void getBookingById_ValidRequest_ReturnsBooking() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRoles(Set.of(new Role(Role.RoleName.USER), new Role(Role.RoleName.ADMIN)));
        setAuthentication(user);

        BookingResponseDto bookingDto = createBookingResponseDto();
        given(bookingService.getBookingById(eq(user.getId()), any(), eq(1L)))
                .willReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER", "ADMIN"})
    void updateBooking_ValidRequest_ReturnsUpdatedBooking() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRoles(Set.of(new Role(Role.RoleName.USER), new Role(Role.RoleName.ADMIN)));
        setAuthentication(user);

        BookingUpdateDto requestDto = createBookingUpdateDto();
        BookingResponseDto responseDto = createBookingResponseDto();
        given(bookingService.updateBookingById(eq(user.getId()),
                any(),
                eq(1L),
                any(BookingUpdateDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER", "ADMIN"})
    void deleteBooking_ValidRequest_ReturnsDeletedBooking() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setRoles(Set.of(new Role(Role.RoleName.USER), new Role(Role.RoleName.ADMIN)));
        setAuthentication(user);

        BookingResponseDto responseDto = createBookingResponseDto();
        given(bookingService.deleteBookingById(eq(user.getId()), any(), eq(1L)))
                .willReturn(responseDto);

        mockMvc.perform(delete("/bookings/{bookingId}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    private void setAuthentication(User user) {
        UserDetails userDetails = withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                        .toArray(SimpleGrantedAuthority[]::new))
                .build();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private BookingRequestDto createBookingRequestDto() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(5));
        dto.setAccommodationID(1L);
        return dto;
    }

    private BookingResponseDto createBookingResponseDto() {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(5));
        dto.setAccommodationID(1L);
        dto.setUserId(1L);
        dto.setStatus(Status.CONFIRMED);
        return dto;
    }

    private BookingUpdateDto createBookingUpdateDto() {
        BookingUpdateDto dto = new BookingUpdateDto();
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(5));
        return dto;
    }
}
