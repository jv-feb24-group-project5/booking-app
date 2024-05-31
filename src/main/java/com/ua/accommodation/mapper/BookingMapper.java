package com.ua.accommodation.mapper;

import com.ua.accommodation.config.MapperConfig;
import com.ua.accommodation.dto.booking.BookingRequestDto;
import com.ua.accommodation.dto.booking.BookingResponseDto;
import com.ua.accommodation.model.Booking;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {

    Booking toEntity(BookingRequestDto requestDto);

    BookingResponseDto toResponseDto(Booking booking);
}
