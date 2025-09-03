package com.osbah.thycase.infra.location.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LocationResponse", description = "Location representation returned by the API")
public record LocationResponse(
        @Schema(description = "Database identifier", example = "1") Long id,
        @Schema(description = "Display name", example = "Sabiha Gokcen Airport") String name,
        @Schema(description = "Country", example = "Turkey") String country,
        @Schema(description = "City", example = "Istanbul") String city,
        @Schema(description = "Unique location code", example = "SAW") String locationCode
) {
}