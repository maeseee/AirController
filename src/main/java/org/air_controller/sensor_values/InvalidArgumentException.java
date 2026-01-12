package org.air_controller.sensor_values;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidArgumentException extends Exception {
    public InvalidArgumentException(String message) {
        super(message);
        log.error(message);
    }

    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
        log.error(message);
    }
}
