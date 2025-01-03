package org.air_controller.sensor_data_persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParseException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(ParseException.class);

    public ParseException(String message) {
        super(message);
        logger.error(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
