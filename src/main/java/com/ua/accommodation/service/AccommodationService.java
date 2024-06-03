package com.ua.accommodation.service;

import com.ua.accommodation.dto.accommodation.AccommodationDto;
import com.ua.accommodation.dto.accommodation.CreateAccommodationRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {

    AccommodationDto create(CreateAccommodationRequestDto requestDto);

    List<AccommodationDto> getAccommodations(Pageable pageable);

    AccommodationDto getAccommodation(Long accommodationId);

    AccommodationDto updateAccommodation(Long id, CreateAccommodationRequestDto requestDto);

    AccommodationDto patchAccommodation(Long id, CreateAccommodationRequestDto requestDto);

    void deleteAccommodation(Long accommodationId);
}
