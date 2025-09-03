package com.osbah.thycase.infra.route.mapper;

import com.osbah.thycase.domain.route.model.Route;
import com.osbah.thycase.infra.location.in.rest.response.LocationResponse;
import com.osbah.thycase.infra.location.mapper.LocationInfraMapper;
import com.osbah.thycase.infra.route.in.rest.response.RouteResponse;
import com.osbah.thycase.infra.transportation.in.rest.response.TransportationResponse;
import com.osbah.thycase.infra.transportation.mapper.TransportationInfraMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RouteInfraMapper {

    RouteInfraMapper INSTANCE = Mappers.getMapper(RouteInfraMapper.class);

    List<RouteResponse> toRouteResponseList(List<Route> routes);

    default RouteResponse toRouteResponse(Route route) {
        LocationResponse originLocation = LocationInfraMapper.INSTANCE.toLocationResponse(route.originLocation());
        LocationResponse destinationLocation = LocationInfraMapper.INSTANCE.toLocationResponse(route.destinationLocation());
        List<TransportationResponse> transportationResponses = TransportationInfraMapper.INSTANCE.toTransportationResponseList(route.transportations());
        return new RouteResponse(originLocation, destinationLocation, transportationResponses);
    }
}
