package com.osbah.thycase.domain.route.model;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.domain.transportation.model.Transportation;

import java.util.List;

public record Route(Location originLocation, Location destinationLocation, List<Transportation> transportations) {
}
