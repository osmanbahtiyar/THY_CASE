package com.osbah.thycase.domain.location.port;

import com.osbah.thycase.domain.location.model.Location;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface LocationPort {

    Slice<Location> getAllLocations(int pageNum, int pageSize, String sortBy, String order);

    Location createLocation(Location location);

    Optional<Location> getLocation(long id);

    void deleteLocation(long id);

    Optional<Location> updateLocation(Location newLocation);
}
