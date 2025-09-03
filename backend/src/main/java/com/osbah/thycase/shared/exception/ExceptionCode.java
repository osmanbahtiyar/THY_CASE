package com.osbah.thycase.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Getter
public enum ExceptionCode {

    LOCATION_CONFLICT(991L, "Location already exists:{}"),
    LOCATION_NOT_FOUND(992L, "Location not found with id:{}"),
    TRANSPORTATION_ORIGIN_AND_DESTINATION_CANNOT_BE_SAME(1001L, "Transportation origin and destination cannot be same"),
    TRANSPORTATION_NOT_FOUND(1002L, "Transportation not found with id:{}"),
    TRANSPORTATION_CONFLICT(1003L, "Transportation already exists by originId:{}, destinationId:{}, type:{}"),
    ORIGIN_AND_DESTINATION_MUST_BE_DIFFERENT(2001L, "Origin and destination must be different");

    private final long errorCode;
    private final String messageTemplate;

    public String formatMessage(String... args) {
        String msg = messageTemplate;
        if (args != null) {
            for (String a : args) {
                msg = StringUtils.replaceOnce(msg, "{}", String.valueOf(a));
            }
        }
        return msg;
    }

}
