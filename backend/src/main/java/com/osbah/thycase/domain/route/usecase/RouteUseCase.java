package com.osbah.thycase.domain.route.usecase;

import com.osbah.thycase.domain.route.model.Route;

import java.util.List;

public interface RouteUseCase {

    List<Route> findRoutesDFS(long originId, long destinationId, Integer operatingDay);
}
