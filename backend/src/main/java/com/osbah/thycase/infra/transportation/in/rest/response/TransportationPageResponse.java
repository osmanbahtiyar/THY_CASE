package com.osbah.thycase.infra.transportation.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "TransportationPageResponse", description = "Paged list of transportations returned by the API")
public record TransportationPageResponse(
        @Schema(description = "Page content") List<TransportationResponse> transportations,
        @Schema(description = "Zero-based page index", example = "0") int pageNum,
        @Schema(description = "Page size (items per page)", example = "20") int pageSize,
        @Schema(description = "Are there more pages after this one?", example = "true") boolean hasNext
) {
}
