package org.air_controller.sensor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorException extends RuntimeException {
    public SensorException(String message, Throwable cause) {
        super(message, cause);
        log.error(message);
    }
}
