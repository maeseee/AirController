package org.air_controller.sensor.qingPing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommunicationException extends Exception {
    private static final Logger logger = LogManager.getLogger(CommunicationException.class);

    public CommunicationException(String message) {
        super(message);
        logger.error(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
