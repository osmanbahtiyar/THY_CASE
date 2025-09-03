package com.osbah.thycase.infra.transportation.in.rest.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OperatingDaysValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOperatingDays {
    String message() default "Operating days must be between 1 and 7";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}