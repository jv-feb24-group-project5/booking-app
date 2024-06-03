package com.ua.accommodation.dto.booking;

import com.ua.accommodation.validation.DateIsNowOrFuture;
import com.ua.accommodation.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
@ValidDateRange(
        startDate = "checkInDate",
        endDate = "checkOutDate",
        message = "checkOutDate must be after checkInDate ")
public class BookingRequestDto {
    @DateIsNowOrFuture
    @NotNull
    private LocalDate checkInDate;
    @DateIsNowOrFuture
    @NotNull
    private LocalDate checkOutDate;
    @NotNull
    private Long accommodationID;
}
