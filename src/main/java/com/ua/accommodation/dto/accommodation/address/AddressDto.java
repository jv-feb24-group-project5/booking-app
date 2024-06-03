package com.ua.accommodation.dto.accommodation.address;

public record AddressDto(
        Long id,
        String street,
        String city,
        String state,
        String zipCode,
        String country
) {
}
