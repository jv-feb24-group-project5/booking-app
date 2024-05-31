package com.ua.accommodation.repository;

import com.ua.accommodation.model.Amenity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    @Query("SELECT a FROM Amenity a WHERE a.name IN :names")
    List<Amenity> findByNameIn(@Param("names") Set<String> names);
}
