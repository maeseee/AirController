package org.airController.sensorValues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvaildArgumentException extends Exception {
    private static final Logger logger = LogManager.getLogger(InvaildArgumentException.class);

    public InvaildArgumentException(String message) {
        super(message);
        logger.error(message);
    }

    public InvaildArgumentException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
