package com.osbah.thycase.infra.location.in.rest;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.domain.location.usecase.LocationUseCase;
import com.osbah.thycase.infra.location.in.rest.request.LocationCreateRequest;
import com.osbah.thycase.infra.location.in.rest.request.LocationUpdateRequest;
import com.osbah.thycase.infra.location.in.rest.response.LocationPageResponse;
import com.osbah.thycase.infra.location.in.rest.response.LocationResponse;
import com.osbah.thycase.infra.location.mapper.LocationInfraMapper;
import com.osbah.thycase.shared.exception.ExceptionCode;
import com.osbah.thycase.shared.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/api/locations")
@Validated
@Tag(name = "Locations", description = "CRUD endpoints for Locations")
public class LocationController {

    private final LocationUseCase locationUseCase;

    @Operation(
            summary = "Create a location",
            description = "Creates a new location and returns the created resource.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Location payload",
            required = true,
            content = @Content(schema = @Schema(implementation = LocationCreateRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody LocationCreateRequest locationCreateRequest) {
        log.debug("LocationController create called with locationCreateRequest:{}", locationCreateRequest);
        Location location = LocationInfraMapper.INSTANCE.toLocationWithEmptyId(locationCreateRequest.payload());
        Location savedLocation = locationUseCase.createLocation(location);
        LocationResponse response = LocationInfraMapper.INSTANCE.toLocationResponse(savedLocation);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "List locations (paged)",
            description = "Returns a slice (page) of locations.")
    @Parameter(name = "pageNum", description = "Zero-based page index", example = "0")
    @Parameter(name = "pageSize", description = "Page size (max 100)", example = "20")
    @Parameter(name = "sortBy", description = "Field to sort by (id, code, name, city, country)", example = "id")
    @Parameter(name = "direction", description = "Sort direction (asc, desc)", example = "asc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = LocationPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<LocationPageResponse> getAll(
            @RequestParam @Min(0) int pageNum,
            @RequestParam @Min(1) @Max(1000)int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.debug("LocationController getAll called with pageNum:{}, pageSize:{}, sortBy:{}, direction:{}", pageNum, pageSize, sortBy, direction);
        Slice<Location> locationSlice = locationUseCase.getAllLocations(pageNum, pageSize, sortBy, direction);
        LocationPageResponse response = LocationInfraMapper.INSTANCE.toLocationPageResponse(locationSlice);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get location by id",
            description = "Returns a location by its id.")
    @Parameter(name = "locationId", description = "Location identifier", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable long locationId) {
        log.debug("LocationController getLocation called with locationId:{}", locationId);

        //@formatter:off
        LocationResponse response = locationUseCase.getLocation(locationId)
                .map(LocationInfraMapper.INSTANCE::toLocationResponse)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.LOCATION_NOT_FOUND.getErrorCode(), ExceptionCode.LOCATION_NOT_FOUND.formatMessage(String.valueOf(locationId))));
        //@formatter:on
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete location",
            description = "Deletes a location by its id.")
    @Parameter(name = "locationId", description = "Location identifier", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable long locationId) {
        log.debug("LocationController deleteLocation called with locationId:{}", locationId);
        locationUseCase.deleteLocation(locationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update location",
            description = "Updates an existing location and returns the updated resource.")
    @Parameter(name = "locationId", description = "Location identifier", example = "1")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Location payload",
            required = true,
            content = @Content(schema = @Schema(implementation = LocationUpdateRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping("/{locationId}")
    public ResponseEntity<LocationResponse> updateLocation(@PathVariable long locationId, @Valid @RequestBody LocationUpdateRequest locationUpdateRequest) {
        log.debug("LocationController updateLocation called with locationId:{}, locationUpdateRequest:{}", locationId, locationUpdateRequest);
        Location newLocation = LocationInfraMapper.INSTANCE.toLocation(locationUpdateRequest.payload(), locationId);
        Location updatedLocation = locationUseCase.updateLocation(newLocation);
        LocationResponse response = LocationInfraMapper.INSTANCE.toLocationResponse(updatedLocation);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
