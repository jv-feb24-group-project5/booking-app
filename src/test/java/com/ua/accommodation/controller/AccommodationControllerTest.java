package com.ua.accommodation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.accommodation.dto.accommodation.AccommodationDto;
import com.ua.accommodation.dto.accommodation.CreateAccommodationRequestDto;
import com.ua.accommodation.dto.accommodation.address.AddressDto;
import com.ua.accommodation.dto.accommodation.address.CreateAddressRequestDto;
import com.ua.accommodation.dto.accommodation.amenity.AmenityDto;
import com.ua.accommodation.dto.accommodation.amenity.CreateAmenityRequestDto;
import com.ua.accommodation.model.Accommodation;
import com.ua.accommodation.service.AccommodationService;
import java.math.BigDecimal;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccommodationControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private AccommodationService accommodationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void addAccommodation_ValidRequest_ReturnsCreatedAccommodation() throws Exception {
        CreateAccommodationRequestDto requestDto = createRequestDto();
        AccommodationDto responseDto = createResponseDto();
        given(accommodationService.create(any(CreateAccommodationRequestDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(post("/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getAllAccommodations_ValidRequest_ReturnsListOfAccommodations() throws Exception {
        List<AccommodationDto> accommodations = Collections.singletonList(createResponseDto());
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        given(accommodationService.getAccommodations(pageable)).willReturn(accommodations);

        mockMvc.perform(get("/accommodations")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accommodations)));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getAccommodationById_ValidRequest_ReturnsAccommodation() throws Exception {
        Long id = 1L;
        AccommodationDto accommodationDto = createResponseDto();
        given(accommodationService.getAccommodation(id)).willReturn(accommodationDto);

        mockMvc.perform(get("/accommodations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accommodationDto)));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void updateAccommodation_ValidRequest_ReturnsUpdatedAccommodation() throws Exception {
        Long id = 1L;
        CreateAccommodationRequestDto requestDto = createRequestDto();
        AccommodationDto responseDto = createResponseDto();
        given(accommodationService.updateAccommodation(eq(id),
                any(CreateAccommodationRequestDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(put("/accommodations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void partiallyUpdateAccommodation_ValidRequest_ReturnsUpdatedAccommodation() throws Exception {
        Long id = 1L;
        CreateAccommodationRequestDto requestDto = createRequestDto();
        AccommodationDto responseDto = createResponseDto();
        given(accommodationService.patchAccommodation(eq(id),
                any(CreateAccommodationRequestDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(patch("/accommodations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    private AccommodationDto createResponseDto() {
        return new AccommodationDto(
                1L,
                Accommodation.Type.HOUSE,
                createLocationResponseDto(),
                "Test size",
                createAmenityResponseDto(),
                BigDecimal.TEN,
                10
        );
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void deleteAccommodation_ValidRequest_ReturnsNoContent() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/accommodations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private CreateAccommodationRequestDto createRequestDto() {
        return new CreateAccommodationRequestDto(
                Accommodation.Type.HOUSE,
                createLocationRequestDto(),
                "Test size",
                createAmenityRequestDto(),
                BigDecimal.TEN,
                10
        );
    }

    private Set<CreateAmenityRequestDto> createAmenityRequestDto() {
        Set<CreateAmenityRequestDto> amenities =
                Set.of(new CreateAmenityRequestDto("Test amenity 1"),
                        new CreateAmenityRequestDto("Test amenity 2"));
        return amenities;

    }

    private Set<AmenityDto> createAmenityResponseDto() {
        Set<AmenityDto> amenityDtos = Set.of(new AmenityDto(1L, "Test amenity 1"),
                new AmenityDto(2L, "Test amenity 2"));
        return amenityDtos;
    }

    private CreateAddressRequestDto createLocationRequestDto() {
        return new CreateAddressRequestDto(
                "Test Street, 123",
                "Test city",
                "Test state",
                "90210",
                "Test country"
        );
    }

    private AddressDto createLocationResponseDto() {
        return new AddressDto(
                1L,
                "Test Street, 123",
                "Test city",
                "Test state",
                "90210",
                "Test country"
        );
    }
}
