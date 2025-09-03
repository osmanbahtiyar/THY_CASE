package com.osbah.thycase.infra.location.in.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationUpsertPayload(
        @Schema(description = "Display name", example = "Sabiha Gokcen Airport") @NotBlank @Size(max = 100) String name,
        @Schema(example = "Turkey") @NotBlank @Size(max = 100) String country,
        @Schema(example = "Istanbul") @NotBlank @Size(max = 100) String city,
        @Schema(description = "Unique code", example = "SAW") @NotBlank @Size(max = 16, min = 3) String locationCode) {
}