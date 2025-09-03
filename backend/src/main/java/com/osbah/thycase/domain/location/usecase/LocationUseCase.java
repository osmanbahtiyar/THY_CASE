package com.osbah.thycase.domain.location.usecase;

import com.osbah.thycase.domain.location.model.Location;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface LocationUseCase {

    Slice<Location> getAllLocations(int pageNum, int pageSize, String sortBy, String order);

    Optional<Location> getLocation(long locationId);

    Location createLocation(Location location);

    void deleteLocation(long locationId);

    Location updateLocation(Location newLocation);
}
