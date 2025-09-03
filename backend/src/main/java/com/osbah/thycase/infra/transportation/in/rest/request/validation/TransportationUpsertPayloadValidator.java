package com.osbah.thycase.infra.transportation.in.rest.request.validation;

import com.osbah.thycase.infra.transportation.in.rest.request.TransportationUpsertPayload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransportationUpsertPayloadValidator implements ConstraintValidator<ValidTransportationUpsertPayload, TransportationUpsertPayload> {

    @Override
    public boolean isValid(TransportationUpsertPayload value, ConstraintValidatorContext context) {
        return !value.originLocationId().equals(value.destinationLocationId());
    }
}