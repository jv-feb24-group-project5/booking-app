package com.ua.accommodation.controller;

import com.ua.accommodation.dto.accommodation.AccommodationDto;
import com.ua.accommodation.dto.accommodation.CreateAccommodationRequestDto;
import com.ua.accommodation.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accommodations")
@Tag(name = "Accommodations Management System", description = "Operations pertaining to "
        + "accommodations in the system")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @Operation(
            summary = "Add a new accommodation",
            description = "Creates a new accommodation entry in the system."
    )
    @PostMapping
    public AccommodationDto addAccommodation(
            @RequestBody @Valid CreateAccommodationRequestDto accommodation
    ) {
        return accommodationService.create(accommodation);
    }

    @Operation(
            summary = "Get a list of all accommodations",
            description = "Retrieves a list of all accommodations available in the system."
    )
    @GetMapping
    public List<AccommodationDto> getAllAccommodations(Pageable pageable) {
        return accommodationService.getAccommodations(pageable);
    }

    @Operation(
            summary = "Get an accommodation by Id",
            description = "Retrieves details of a specific accommodation by its unique identifier."
    )
    @GetMapping("/{id}")
    public AccommodationDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.getAccommodation(id);
    }

    @Operation(
            summary = "Update an accommodation",
            description = "Updates details of an existing accommodation."
    )
    @PutMapping("/{id}")
    public AccommodationDto updateAccommodation(
            @PathVariable Long id,
            @RequestBody @Valid CreateAccommodationRequestDto updatedAccommodation) {
        return accommodationService.updateAccommodation(id, updatedAccommodation);
    }

    @Operation(
            summary = "Partially update an accommodation",
            description = "Partially updates details of an existing accommodation. "
                    + "Can accept only fields that need to be changed."
    )
    @PatchMapping("/{id}")
    public AccommodationDto partiallyUpdateAccommodation(
            @PathVariable Long id,
            @RequestBody @Valid CreateAccommodationRequestDto updatedAccommodation) {
        return accommodationService.patchAccommodation(id, updatedAccommodation);
    }

    @Operation(
            summary = "Delete an accommodation",
            description = "Deletes an existing accommodation from the system."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteAccommodation(id);
    }
}
