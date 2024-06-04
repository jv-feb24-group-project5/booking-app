package com.ua.accommodation.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ua.accommodation.dto.accommodation.AccommodationDto;
import com.ua.accommodation.dto.accommodation.CreateAccommodationRequestDto;
import com.ua.accommodation.dto.accommodation.address.AddressDto;
import com.ua.accommodation.dto.accommodation.address.CreateAddressRequestDto;
import com.ua.accommodation.dto.accommodation.amenity.AmenityDto;
import com.ua.accommodation.dto.accommodation.amenity.CreateAmenityRequestDto;
import com.ua.accommodation.mapper.AccommodationMapper;
import com.ua.accommodation.model.Accommodation;
import com.ua.accommodation.model.Address;
import com.ua.accommodation.model.Amenity;
import com.ua.accommodation.repository.AccommodationRepository;
import com.ua.accommodation.repository.AddressRepository;
import com.ua.accommodation.repository.AmenityRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceImplTest {

    @InjectMocks
    private AccommodationServiceImpl accommodationService;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private AccommodationMapper accommodationMapper;

    private CreateAddressRequestDto createAddressRequestDto;
    private Set<CreateAmenityRequestDto> createAmenityRequestDtoSet;
    private CreateAccommodationRequestDto createAccommodationRequestDto;
    private Accommodation accommodation;
    private AccommodationDto accommodationDto;

    @BeforeEach
    void setUp() {
        createAddressRequestDto = new CreateAddressRequestDto(
                "street", "city", "state", "00000", "country"
        );

        createAmenityRequestDtoSet = Set.of(new CreateAmenityRequestDto("name"));

        createAccommodationRequestDto = new CreateAccommodationRequestDto(
                Accommodation.Type.HOUSE, createAddressRequestDto, "size",
                createAmenityRequestDtoSet, new BigDecimal(10), 1
        );

        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setLocation(new Address());
        accommodation.setSize("size");
        accommodation.setAmenities(Set.of(new Amenity()));
        accommodation.setDailyRate(new BigDecimal(10));
        accommodation.setAvailability(1);

        accommodationDto = new AccommodationDto(
                accommodation.getId(), accommodation.getType(),
                new AddressDto(1L, "street", "city", "state", "00000", "country"),
                accommodation.getSize(), Set.of(new AmenityDto(1L, "name")),
                accommodation.getDailyRate(), accommodation.getAvailability()
        );
    }

    @Test
    @DisplayName("Create Accommodation with valid data should return valid accommodation")
    void create_WithValidData_ShouldReturnValidDto() {
        when(accommodationMapper.toModel(createAccommodationRequestDto))
                .thenReturn(accommodation);
        when(accommodationRepository.save(any(Accommodation.class)))
                .thenReturn(accommodation);
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(accommodationDto);

        AccommodationDto result = accommodationService.create(createAccommodationRequestDto);

        assertNotNull(result);
        assertEquals(accommodationDto, result);
        verify(accommodationRepository).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Get all accommodations should return a list of accommodations")
    void getAllAccommodations_ShouldReturnListOfAccommodations() {
        List<Accommodation> accommodations = List.of(accommodation, new Accommodation());
        when(accommodationRepository.findAllWithAddressAndAmenities(any(PageRequest.class)))
                .thenReturn(accommodations);
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(accommodationDto);

        List<AccommodationDto> result = accommodationService.getAccommodations(
                PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accommodationRepository)
                .findAllWithAddressAndAmenities(any(PageRequest.class));
    }

    @Test
    @DisplayName("Get accommodation by ID should return the accommodation")
    void getAccommodationById_ShouldReturnAccommodation() {
        when(accommodationRepository.findByIdWithAddressAndAmenities(accommodation.getId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(accommodationDto);

        AccommodationDto result = accommodationService.getAccommodation(accommodation.getId());

        assertNotNull(result);
        assertEquals(accommodationDto, result);
        verify(accommodationRepository)
                .findByIdWithAddressAndAmenities(accommodation.getId());
    }

    @Test
    @DisplayName("Update accommodation should save and return the updated accommodation")
    void updateAccommodation_ShouldSaveAndReturnUpdatedAccommodation() {
        when(accommodationRepository.findByIdWithAddressAndAmenities(accommodation.getId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toModel(createAccommodationRequestDto))
                .thenReturn(accommodation);
        when(accommodationRepository.save(any(Accommodation.class)))
                .thenReturn(accommodation);
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(accommodationDto);

        AccommodationDto result = accommodationService.updateAccommodation(
                accommodation.getId(), createAccommodationRequestDto
        );

        assertNotNull(result);
        assertEquals(accommodationDto, result);
        verify(accommodationRepository).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Patch accommodation should save and return the updated accommodation")
    void patchAccommodation_ReturnsUpdatedAccommodation() {
        when(accommodationRepository.findByIdWithAddressAndAmenities(accommodation.getId()))
                .thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(any(Accommodation.class)))
                .thenReturn(accommodation);
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(accommodationDto);

        AccommodationDto result = accommodationService.patchAccommodation(
                accommodation.getId(), createAccommodationRequestDto
        );

        assertNotNull(result);
        assertEquals(accommodationDto, result);
        verify(accommodationRepository).save(any(Accommodation.class));
    }

    @Test
    @DisplayName("Delete accommodation should remove the accommodation")
    void deleteAccommodation_ShouldRemoveAccommodation() {
        when(accommodationRepository.existsById(accommodation.getId()))
                .thenReturn(true);
        doNothing().when(accommodationRepository)
                .deleteById(accommodation.getId());

        accommodationService.deleteAccommodation(accommodation.getId());

        verify(accommodationRepository).deleteById(accommodation.getId());
    }
}
