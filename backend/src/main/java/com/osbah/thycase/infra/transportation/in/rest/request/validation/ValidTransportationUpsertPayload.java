package com.osbah.thycase.infra.transportation.in.rest.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TransportationUpsertPayloadValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTransportationUpsertPayload {
    String message() default "Origin and destination location must be different";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}