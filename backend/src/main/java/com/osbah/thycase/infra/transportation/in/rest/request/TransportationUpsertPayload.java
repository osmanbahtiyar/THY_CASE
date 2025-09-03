package com.osbah.thycase.infra.transportation.in.rest.request;

import com.osbah.thycase.domain.transportation.model.TransportationType;
import com.osbah.thycase.infra.transportation.in.rest.request.validation.ValidOperatingDays;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Schema(name = "TransportationUpsertPayload", description = "Payload used for creating or updating a transportation link")
public record TransportationUpsertPayload(
        @Schema(description = "Origin location identifier", example = "1") @NotNull Long originLocationId,
        @Schema(description = "Destination location identifier", example = "2") @NotNull Long destinationLocationId,
        @Schema(description = "Transportation type", example = "FLIGHT") @NotNull TransportationType transportationType,
        @Schema(description = "Operating days of week (1=Mon ... 7=Sun)", example = "[1,2,3,4,5]") @ValidOperatingDays Set<Integer> operatingDays) {
}