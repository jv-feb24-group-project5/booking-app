package com.ua.accommodation.mapper;

import com.ua.accommodation.config.MapperConfig;
import com.ua.accommodation.dto.accommodation.AccommodationDto;
import com.ua.accommodation.dto.accommodation.CreateAccommodationRequestDto;
import com.ua.accommodation.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(
        config = MapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccommodationMapper {

    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    AccommodationDto toDto(Accommodation accommodation);

    void updateAccommodationFromDto(CreateAccommodationRequestDto requestDto,
                                    @MappingTarget Accommodation accommodation);
}
