package org.air_controller.sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SensorException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(SensorException.class);

    public SensorException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
