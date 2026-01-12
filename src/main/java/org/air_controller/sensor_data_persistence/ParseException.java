package org.air_controller.sensor_data_persistence;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParseException extends RuntimeException {
    public ParseException(String message, Throwable cause) {
        super(message, cause);
        log.error(message);
    }
}
