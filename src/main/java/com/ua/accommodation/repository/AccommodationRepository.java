package com.ua.accommodation.repository;

import com.ua.accommodation.model.Accommodation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query("SELECT a FROM Accommodation a "
            + "LEFT JOIN FETCH a.location "
            + "LEFT JOIN FETCH a.amenities")
    List<Accommodation> findAllWithAddressAndAmenities(Pageable pageable);

    @Query("SELECT a FROM Accommodation a "
            + "LEFT JOIN FETCH a.location "
            + "LEFT JOIN FETCH a.amenities "
            + "WHERE a.id = :id")
    Optional<Accommodation> findByIdWithAddressAndAmenities(@Param("id") Long id);
}
