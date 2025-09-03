package com.osbah.thycase.infra.location.in.rest.request;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;

public record LocationCreateRequest(@JsonUnwrapped @Valid LocationUpsertPayload payload) {
}