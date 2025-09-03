package com.osbah.thycase.domain.transportation.model;

import com.osbah.thycase.domain.location.model.Location;
import com.osbah.thycase.shared.exception.ExceptionCode;
import com.osbah.thycase.shared.exception.ValidationException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.time.DayOfWeek;
import java.util.Set;


@Log4j2
@Data
public class Transportation {
    private Long id;
    private Location originLocation;
    private Location destinationLocation;
    private TransportationType transportationType;
    private Set<DayOfWeek> operatingDays;

    public void validate() {
        validateOriginAndDestinationLocationDifferent();
    }

    private void validateOriginAndDestinationLocationDifferent() {
        if (originLocation.getId().equals(destinationLocation.getId())) {
            log.error("Origin and destination locations cannot be the same");
            throw new ValidationException(ExceptionCode.TRANSPORTATION_ORIGIN_AND_DESTINATION_CANNOT_BE_SAME.getErrorCode(), ExceptionCode.TRANSPORTATION_ORIGIN_AND_DESTINATION_CANNOT_BE_SAME.formatMessage());
        }
    }
}
