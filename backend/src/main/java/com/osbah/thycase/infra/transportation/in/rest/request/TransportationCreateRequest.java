package com.osbah.thycase.infra.transportation.in.rest.request;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.osbah.thycase.infra.transportation.in.rest.request.validation.ValidTransportationUpsertPayload;
import jakarta.validation.Valid;

public record TransportationCreateRequest(@JsonUnwrapped @Valid @ValidTransportationUpsertPayload TransportationUpsertPayload payload) {
}
