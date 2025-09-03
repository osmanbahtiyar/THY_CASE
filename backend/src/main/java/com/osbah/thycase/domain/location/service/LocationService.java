package com.osbah.thycase.domain.location.service;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.domain.location.port.LocationPort;
import com.osbah.thycase.domain.location.usecase.LocationUseCase;
import com.osbah.thycase.shared.exception.ExceptionCode;
import com.osbah.thycase.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LocationService implements LocationUseCase {

    private final LocationPort locationPort;

    @Override
    public Slice<Location> getAllLocations(int pageNum, int pageSize, String sortBy, String order) {
        return locationPort.getAllLocations(pageNum, pageSize, sortBy, order);
    }

    @Override
    public Optional<Location> getLocation(long locationId) {
        return locationPort.getLocation(locationId);
    }

    @Override
    public Location createLocation(Location location) {
        return locationPort.createLocation(location);
    }

    @Override
    public void deleteLocation(long locationId) {
        locationPort.deleteLocation(locationId);
    }

    @Override
    public Location updateLocation(final Location newLocation) {
        return locationPort.updateLocation(newLocation)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.LOCATION_NOT_FOUND.getErrorCode(), ExceptionCode.LOCATION_NOT_FOUND.formatMessage(String.valueOf(newLocation.getId()))));
    }
}
