package com.ua.accommodation.dto.accommodation;

import com.ua.accommodation.dto.accommodation.address.CreateAddressRequestDto;
import com.ua.accommodation.dto.accommodation.amenity.CreateAmenityRequestDto;
import com.ua.accommodation.model.Accommodation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

public record CreateAccommodationRequestDto(
        @NotNull(message = "Type is required") Accommodation.Type type,

        @Valid CreateAddressRequestDto location,

        @NotBlank(message = "Size is required") String size,

        @NotNull(message = "Amenities are required")
        Set<@Valid CreateAmenityRequestDto> amenities,

        @NotNull(message = "Daily rate is required")
        @PositiveOrZero(
                message = "Daily rate must be positive or zero") BigDecimal dailyRate,

        @NotNull(message = "Availability is required")
        @PositiveOrZero(message = "Availability must be positive or zero") Integer availability
) {
}
