package com.ua.accommodation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.springframework.beans.BeanWrapperImpl;

public class DateRangeValidator
        implements ConstraintValidator<ValidDateRange, Object> {
    private String startDate;
    private String endDate;

    public void initialize(ValidDateRange constraintAnnotation) {
        this.startDate = constraintAnnotation.startDate();
        this.endDate = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate startDateValue = (LocalDate) new BeanWrapperImpl(value)
                .getPropertyValue(startDate);
        LocalDate endDateValue = (LocalDate) new BeanWrapperImpl(value)
                .getPropertyValue(endDate);

        return startDateValue.isBefore(endDateValue);
    }
}
