package com.ua.accommodation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateIsNowOrFutureValidator
        implements ConstraintValidator<DateIsNowOrFuture, LocalDate> {

    @Override
    public void initialize(DateIsNowOrFuture constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return date.isEqual(LocalDate.now()) || date.isAfter(LocalDate.now());
    }
}
