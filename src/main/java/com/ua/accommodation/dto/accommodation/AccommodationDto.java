package com.ua.accommodation.dto.accommodation;

import com.ua.accommodation.dto.accommodation.address.AddressDto;
import com.ua.accommodation.dto.accommodation.amenity.AmenityDto;
import com.ua.accommodation.model.Accommodation.Type;
import java.math.BigDecimal;
import java.util.Set;

public record AccommodationDto(
        Long id,
        Type type,
        AddressDto location,
        String size,
        Set<AmenityDto> amenities,
        BigDecimal dailyRate,
        Integer availability
) {
}
