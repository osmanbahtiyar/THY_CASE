package com.osbah.thycase.infra.route.in.rest;

import com.osbah.thycase.domain.route.model.Route;
import com.osbah.thycase.domain.route.usecase.RouteUseCase;
import com.osbah.thycase.infra.route.in.rest.response.RouteResponse;
import com.osbah.thycase.infra.route.mapper.RouteInfraMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Route Controller", description = "API for finding transportation routes between locations")
@RequiredArgsConstructor
@Log4j2
@Validated
public class RouteController {

    private final RouteUseCase routeUseCase;

    @Operation(
            summary = "Find routes between two locations",
            description = "Returns all valid routes from the given origin to the given destination for the specified date. A valid route consists of up to 3 legs with exactly 1 flight, optionally 1 non-flight leg before and/or after the flight. Results are filtered by the operating day derived from the provided date.")
    @Parameters({
            @Parameter(name = "originLocationId", description = "ID of the origin location", required = true, example = "1001"),
            @Parameter(name = "destinationLocationId", description = "ID of the destination location", required = true, example = "2001"),
            @Parameter(name = "date", description = "Date of travel in ISO-8601 format (yyyy-MM-dd). The route legs must operate on this day.", required = true, example = "2025-09-05")
    })
    @ApiResponse(
            responseCode = "200",
            description = "Routes found",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RouteResponse.class))
            )
    )
    @GetMapping("/find")
    public ResponseEntity<List<RouteResponse>> findRoutes(@RequestParam @NotNull long originLocationId,
                                                          @RequestParam @NotNull long destinationLocationId,
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("RouteController: findRoutes called with originLocationId:{}, destinationLocationId:{}, date:{}", originLocationId, destinationLocationId, date);
        final Integer dayOfWeek = date == null ? null : date.getDayOfWeek().getValue();
        List<Route> routes = routeUseCase.findRoutesDFS(originLocationId, destinationLocationId, dayOfWeek);
        List<RouteResponse> response = RouteInfraMapper.INSTANCE.toRouteResponseList(routes);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
