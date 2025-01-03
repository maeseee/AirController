package org.air_controller.sensorValues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvalidArgumentException extends Exception {
    private static final Logger logger = LogManager.getLogger(InvalidArgumentException.class);

    public InvalidArgumentException(String message) {
        super(message);
        logger.error(message);
    }

    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
