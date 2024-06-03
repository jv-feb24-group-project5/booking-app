package com.ua.accommodation.dto.accommodation.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateAddressRequestDto(
        @NotBlank(message = "Street is required") String street,

        @NotBlank(message = "City is required") String city,

        @NotBlank(message = "State is required") String state,

        @NotBlank
        @Pattern(
                regexp = "\\d{5}(-\\d{4})?",
                message = "Zip code must be 5 or 9 digits"
        ) String zipCode,

        @NotBlank(message = "Country is required") String country
) {
}
