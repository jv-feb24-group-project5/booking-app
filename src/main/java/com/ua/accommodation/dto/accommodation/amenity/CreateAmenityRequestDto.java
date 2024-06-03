package com.ua.accommodation.dto.accommodation.amenity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAmenityRequestDto(
        @NotBlank(message = "Name is required")
        @Size(
                min = 2, max = 50, message = "Name must be between 2 and 50 characters"
        ) String name
) {
}
