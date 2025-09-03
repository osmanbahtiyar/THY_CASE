package com.osbah.thycase.infra.transportation.in.rest.response;

import com.osbah.thycase.domain.transportation.model.TransportationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "TransportationResponse", description = "Transportation resource returned by the API")
public record TransportationResponse(
        @Schema(description = "Database identifier", example = "101") Long id,
        @Schema(description = "Origin location details") TransportationLocationResponse originLocation,
        @Schema(description = "Destination location details") TransportationLocationResponse destinationLocation,
        @Schema(description = "Transportation type", example = "FLIGHT") TransportationType transportationType,
        @Schema(description = "Operating days of week (1=Mon … 7=Sun)", example = "[1,2,3,4,5]") List<Integer> operatingDays
) {
}