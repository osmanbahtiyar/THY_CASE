package com.osbah.thycase.infra.location.out.jpa;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.domain.location.port.LocationPort;
import com.osbah.thycase.infra.location.mapper.LocationInfraMapper;
import com.osbah.thycase.shared.exception.ConflictException;
import com.osbah.thycase.shared.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
public class LocationDataAdapter implements LocationPort {

    private final LocationJpaRepository locationRepository;

    private static PageRequest getPageRequest(int pageNum, int pageSize, String sortBy, String direction) {
        String property = getSortingProperty(sortBy);
        Sort sort = getSortingDirection(direction, property);
        return PageRequest.of(pageNum, pageSize, sort);
    }

    private static Sort getSortingDirection(String direction, String property) {
        return "desc".equalsIgnoreCase(direction)
                ? Sort.by(property).descending()
                : Sort.by(property).ascending();
    }

    private static String getSortingProperty(String sortBy) {
        String property;
        if ("id".equalsIgnoreCase(sortBy)) {
            property = "id";
        } else if ("code".equalsIgnoreCase(sortBy)) {
            property = "code";
        } else if ("name".equalsIgnoreCase(sortBy) || "locationName".equalsIgnoreCase(sortBy)) {
            property = "name";
        } else if ("city".equalsIgnoreCase(sortBy)) {
            property = "city";
        } else if ("country".equalsIgnoreCase(sortBy)) {
            property = "country";
        } else {
            property = "id";
        }
        return property;
    }

    @Override
    public Slice<Location> getAllLocations(int pageNum, int pageSize, String sortBy, String direction) {
        log.debug("LocationDataAdapter: getAllLocations called with pageNum:{} pageSize:{}, sortBy:{}, direction:{}", pageNum, pageSize, sortBy, direction);
        PageRequest pageRequest = getPageRequest(pageNum, pageSize, sortBy, direction);

        Slice<Location> locations = locationRepository.findAll(pageRequest)
                .map(LocationInfraMapper.INSTANCE::toLocation);

        log.trace("LocationDataAdapter: getAllLocations returned {}", locations);
        return locations;
    }

    @Override
    @Transactional
    public Location createLocation(Location location) {
        log.debug("LocationDataAdapter: createLocation called with {}", location);
        checkLocationAlreadyExists(location);
        return saveLocation(location);
    }

    @Override
    public Optional<Location> getLocation(long id) {
        log.debug("LocationDataAdapter: getLocation called with id:{}", id);
        return locationRepository.findById(id).map(LocationInfraMapper.INSTANCE::toLocation);
    }

    @Override
    @Transactional
    public void deleteLocation(long id) {
        log.debug("LocationDataAdapter: deleteLocation called with id:{}", id);
        locationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Location> updateLocation(Location newLocation) {
        return locationRepository.findById(newLocation.getId()).map(locationEntity -> {
            LocationInfraMapper.INSTANCE.updateEntityFromDomain(newLocation, locationEntity);
            LocationEntity saved = locationRepository.saveAndFlush(locationEntity);
            return LocationInfraMapper.INSTANCE.toLocation(saved);
        });
    }

    private Location saveLocation(Location location) {
        LocationEntity entity = LocationInfraMapper.INSTANCE.toLocationEntity(location);
        try {
            LocationEntity savedLocation = locationRepository.saveAndFlush(entity);
            log.info("Location saved with id:{}", savedLocation);
            return LocationInfraMapper.INSTANCE.toLocation(savedLocation);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition on unique location_code: {}", location.getCode(), e);
            throw new ConflictException(ExceptionCode.LOCATION_CONFLICT.getErrorCode(), ExceptionCode.LOCATION_CONFLICT.formatMessage(location.getCode()), e);
        }
    }

    private void checkLocationAlreadyExists(Location location) {
        if (locationRepository.existsByCode(location.getCode())) {
            log.debug("Location already exists by pre-check: {}", location.getCode());
            throw new ConflictException(ExceptionCode.LOCATION_CONFLICT.getErrorCode(), ExceptionCode.LOCATION_CONFLICT.formatMessage(location.getCode()));
        }
    }


}
