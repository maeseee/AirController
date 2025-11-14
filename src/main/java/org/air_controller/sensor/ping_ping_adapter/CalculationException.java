package org.air_controller.sensor.ping_ping_adapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalculationException extends Exception {
    private static final Logger logger = LogManager.getLogger(CalculationException.class);

    public CalculationException(String message) {
        super(message);
        logger.error(message);
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
