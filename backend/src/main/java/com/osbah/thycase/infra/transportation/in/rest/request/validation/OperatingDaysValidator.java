package com.osbah.thycase.infra.transportation.in.rest.request.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class OperatingDaysValidator implements ConstraintValidator<ValidOperatingDays, Collection<Integer>> {

    @Override
    public boolean isValid(Collection<Integer> value, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(value)) {
            return true;
        }
        return value.stream().allMatch(day -> day >= 1 && day <= 7);
    }
}