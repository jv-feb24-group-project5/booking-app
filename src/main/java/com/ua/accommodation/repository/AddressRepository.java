package com.ua.accommodation.repository;

import com.ua.accommodation.model.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByStreetAndCityAndStateAndZipCodeAndCountry(
            String street, String city, String state, String zipCode, String country
    );
}
