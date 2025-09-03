package com.osbah.thycase.infra.transportation.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TransportationLocationResponse", description = "Location summary used inside Transportation responses")
public record TransportationLocationResponse(
        @Schema(description = "Database identifier", example = "1") Long id,
        @Schema(description = "Display name", example = "Sabiha Gokcen Airport") String name,
        @Schema(description = "Country", example = "Turkey") String country,
        @Schema(description = "City", example = "Istanbul") String city,
        @Schema(description = "Unique location code", example = "SAW") String locationCode
) {
}
