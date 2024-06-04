package com.ua.accommodation.service.impl;

import com.ua.accommodation.dto.accommodation.AccommodationDto;
import com.ua.accommodation.dto.accommodation.CreateAccommodationRequestDto;
import com.ua.accommodation.exception.EntityNotFoundException;
import com.ua.accommodation.mapper.AccommodationMapper;
import com.ua.accommodation.model.Accommodation;
import com.ua.accommodation.model.Address;
import com.ua.accommodation.model.Amenity;
import com.ua.accommodation.repository.AccommodationRepository;
import com.ua.accommodation.repository.AddressRepository;
import com.ua.accommodation.repository.AmenityRepository;
import com.ua.accommodation.service.AccommodationService;
import com.ua.accommodation.service.event.NotificationEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final AmenityRepository amenityRepository;
    private final AccommodationMapper accommodationMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public AccommodationDto create(CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        mergeExistingAddress(accommodation);
        mergeExistingAmenities(accommodation);
        accommodationRepository.save(accommodation);
        publishEvent(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public List<AccommodationDto> getAccommodations(Pageable pageable) {
        List<Accommodation> accommodations =
                accommodationRepository.findAllWithAddressAndAmenities(pageable);
        return accommodations.stream().map(accommodationMapper::toDto).toList();
    }

    @Override
    public AccommodationDto getAccommodation(Long accommodationId) {
        return accommodationMapper.toDto(
                getAccommodationWithAddressAndAmenitiesById(accommodationId)
        );
    }

    @Override
    public AccommodationDto updateAccommodation(Long id, CreateAccommodationRequestDto requestDto) {
        getAccommodationWithAddressAndAmenitiesById(id);
        Accommodation model = accommodationMapper.toModel(requestDto);
        model.setId(id);
        mergeExistingAmenities(model);
        mergeExistingAddress(model);
        accommodationRepository.save(model);
        publishEvent(model);
        return accommodationMapper.toDto(model);
    }

    @Override
    public AccommodationDto patchAccommodation(Long id, CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = getAccommodationWithAddressAndAmenitiesById(id);
        accommodationMapper.updateAccommodationFromDto(requestDto, accommodation);
        mergeExistingAmenities(accommodation);
        mergeExistingAddress(accommodation);
        accommodationRepository.save(accommodation);
        publishEvent(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public void deleteAccommodation(Long accommodationId) {
        if (!accommodationRepository.existsById(accommodationId)) {
            throw new EntityNotFoundException(
                    "Accommodation with id " + accommodationId + " not found");
        }
        accommodationRepository.deleteById(accommodationId);
        eventPublisher.publishEvent(new NotificationEvent(this,
                String.format("Accommodation with id: %d was deleted.", accommodationId)));
    }

    private Accommodation getAccommodationWithAddressAndAmenitiesById(Long accommodationId) {
        Optional<Accommodation> accommodationOptional =
                accommodationRepository.findByIdWithAddressAndAmenities(accommodationId);
        return accommodationOptional
                .orElseThrow(() -> new EntityNotFoundException(
                        "No accommodation found with id " + accommodationId));
    }

    private void mergeExistingAddress(Accommodation accommodation) {
        Address address = accommodation.getLocation();
        Optional<Address> existingAddress =
                addressRepository.findByStreetAndCityAndStateAndZipCodeAndCountry(
                        address.getStreet(), address.getCity(), address.getState(),
                        address.getZipCode(),
                        address.getCountry());
        existingAddress.ifPresent(accommodation::setLocation);
    }

    private void mergeExistingAmenities(Accommodation accommodation) {
        Set<String> amenityNames = accommodation.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
        Map<String, Amenity> existingAmenities =
                amenityRepository.findByNameIn(amenityNames).stream()
                        .collect(Collectors.toMap(Amenity::getName, amenity -> amenity));
        Set<Amenity> updatedAmenities = new HashSet<>();
        for (Amenity amenity : accommodation.getAmenities()) {
            updatedAmenities.add(existingAmenities.getOrDefault(amenity.getName(), amenity));
        }
        accommodation.setAmenities(updatedAmenities);
    }

    private void publishEvent(Accommodation accommodation) {
        StringBuilder builder = new StringBuilder();
        String message = builder.append("Accommodation update!")
                .append(System.lineSeparator())
                .append("Id: ").append(accommodation.getId())
                .append(System.lineSeparator())
                .append("Type: ").append(accommodation.getType())
                .append(System.lineSeparator())
                .append("Size: ").append(accommodation.getSize())
                .append(System.lineSeparator())
                .append("Location: ").append(accommodation.getLocation().toString())
                .append(System.lineSeparator())
                .append("Daily rate: ").append(accommodation.getDailyRate()).append("$")
                .append(System.lineSeparator())
                .append("Amenities: ").append(
                        accommodation.getAmenities().stream()
                                .map(Amenity::getName)
                                .collect(Collectors.joining(", "))
                ).append(System.lineSeparator())
                .append("Sleep units: ").append(accommodation.getAvailability())
                .toString();
        NotificationEvent event = new NotificationEvent(this, message);
        eventPublisher.publishEvent(event);
    }
}
