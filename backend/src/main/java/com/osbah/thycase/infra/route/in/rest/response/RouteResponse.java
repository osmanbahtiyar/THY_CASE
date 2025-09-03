package com.osbah.thycase.infra.route.in.rest.response;

import com.osbah.thycase.infra.location.in.rest.response.LocationResponse;
import com.osbah.thycase.infra.transportation.in.rest.response.TransportationResponse;

import java.util.List;

public record RouteResponse(LocationResponse originLocation, LocationResponse destinationLocation,
                            List<TransportationResponse> transportations) {
}
