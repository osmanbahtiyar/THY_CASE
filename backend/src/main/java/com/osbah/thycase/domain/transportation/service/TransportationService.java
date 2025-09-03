package com.osbah.thycase.domain.transportation.service;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.domain.location.port.LocationPort;
import com.osbah.thycase.domain.transportation.model.Transportation;
import com.osbah.thycase.domain.transportation.port.TransportationPort;
import com.osbah.thycase.domain.transportation.usecase.TransportationUseCase;
import com.osbah.thycase.shared.exception.ExceptionCode;
import com.osbah.thycase.shared.exception.NotFoundException;
import com.osbah.thycase.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransportationService implements TransportationUseCase {
    private final LocationPort locationPort;
    private final TransportationPort transportationPort;

    @Override
    public Slice<Transportation> getAllTransportations(int pageNum, int pageSize) {
        return transportationPort.getAllTransportations(pageNum, pageSize);
    }

    @Override
    public Optional<Transportation> getTransportation(long transportationId) {
        return transportationPort.getTransportation(transportationId);
    }

    @Override
    public Transportation createTransportation(Transportation transportation) {
        validateAndFillLocations(transportation);
        return transportationPort.createTransportation(transportation);
    }


    @Override
    public void deleteTransportation(long transportationId) {
        transportationPort.deleteTransportation(transportationId);
    }

    @Override
    public Transportation updateTransportation(long transportationId, Transportation transportation) {
        validateAndFillLocations(transportation);
        return transportationPort.updateTransportation(transportationId, transportation)
                .orElseThrow(() -> new NotFoundException(ExceptionCode.TRANSPORTATION_NOT_FOUND.getErrorCode(), ExceptionCode.TRANSPORTATION_NOT_FOUND.formatMessage(String.valueOf(transportationId))));
    }

    private Location getLocation(Long locationId) {
        return locationPort.getLocation(locationId).orElseThrow(() -> new ValidationException(ExceptionCode.LOCATION_NOT_FOUND.getErrorCode(), ExceptionCode.LOCATION_NOT_FOUND.formatMessage(locationId.toString())));
    }

    private void validateAndFillLocations(Transportation transportation) {
        final Location originLocation = getLocation(transportation.getOriginLocation().getId());
        final Location destinationLocation = getLocation(transportation.getDestinationLocation().getId());
        transportation.setOriginLocation(originLocation);
        transportation.setDestinationLocation(destinationLocation);
        transportation.validate();
    }
}
