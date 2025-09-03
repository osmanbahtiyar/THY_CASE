package com.osbah.thycase.infra.transportation.in.rest;

import com.osbah.thycase.domain.transportation.model.Transportation;
import com.osbah.thycase.domain.transportation.model.TransportationType;
import com.osbah.thycase.domain.transportation.usecase.TransportationUseCase;
import com.osbah.thycase.infra.transportation.in.rest.request.TransportationCreateRequest;
import com.osbah.thycase.infra.transportation.in.rest.request.TransportationUpdateRequest;
import com.osbah.thycase.infra.transportation.in.rest.response.TransportationPageResponse;
import com.osbah.thycase.infra.transportation.in.rest.response.TransportationResponse;
import com.osbah.thycase.infra.transportation.mapper.TransportationInfraMapper;
import com.osbah.thycase.shared.exception.ExceptionCode;
import com.osbah.thycase.shared.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transportations")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Transportations", description = "Operations on transportation resources")
public class TransportationController {

    private final TransportationUseCase transportationUseCase;

    @Operation(summary = "Create transportation", description = "Creates a transportation between two locations.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = TransportationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TransportationResponse> create(@Valid @RequestBody TransportationCreateRequest request) {
        log.debug("TransportationController create called with request: {}", request);
        Transportation transportation = TransportationInfraMapper.INSTANCE.toTransportation(request.payload());
        Transportation savedTransportation = transportationUseCase.createTransportation(transportation);
        TransportationResponse response = TransportationInfraMapper.INSTANCE.toTransportationResponse(savedTransportation);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "List transportations (paged)", description = "Returns a slice of transportations.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TransportationPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<TransportationPageResponse> getAll(@RequestParam int pageNum, @RequestParam int pageSize) {
        log.debug("TransportationController getAll called with pageNum:{}, pageSize:{}", pageNum, pageSize);
        Slice<Transportation> transportationSlice = transportationUseCase.getAllTransportations(pageNum, pageSize);
        TransportationPageResponse response = TransportationInfraMapper.INSTANCE.toTransportationPageResponse(transportationSlice);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get transportation by id", description = "Returns a transportation by its id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TransportationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransportationResponse> getTransportation(@PathVariable long id) {
        log.debug("TransportationController getTransportation called with id: {}", id);
        TransportationResponse response = transportationUseCase.getTransportation(id).map(TransportationInfraMapper.INSTANCE::toTransportationResponse).orElseThrow(() -> new NotFoundException(ExceptionCode.TRANSPORTATION_NOT_FOUND.getErrorCode(), ExceptionCode.TRANSPORTATION_NOT_FOUND.formatMessage(String.valueOf(id))));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete transportation", description = "Deletes a transportation by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransportation(@PathVariable long id) {
        log.debug("TransportationController deleteTransportation called with id: {}", id);
        transportationUseCase.deleteTransportation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update transportation", description = "Replaces an existing transportation with the provided payload.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TransportationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransportationResponse> updateTransportation(@PathVariable long id, @Valid @RequestBody TransportationUpdateRequest transportationUpdateRequest) {
        log.debug("TransportationController updateTransportation called with id: {}, transportationUpdateRequest:{}", id, transportationUpdateRequest);
        Transportation newTransportation = TransportationInfraMapper.INSTANCE.toTransportation(transportationUpdateRequest.payload());
        Transportation updatedTransportation = transportationUseCase.updateTransportation(id, newTransportation);
        TransportationResponse response = TransportationInfraMapper.INSTANCE.toTransportationResponse(updatedTransportation);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get Transportation Types", description = "Get available transportation types")
    @GetMapping("/types")
    public ResponseEntity<Set<String>> getTransportationTypes() {
        log.debug("TransportationController getTransportationTypes called");
        Set<String> types = Arrays.stream(TransportationType.values()).map(TransportationType::name).collect(Collectors.toSet());
        return new ResponseEntity<>(types, HttpStatus.OK);
    }
}