package org.air_controller.sensor.qing_ping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommunicationException extends Exception {
    private static final Logger logger = LogManager.getLogger(CommunicationException.class);

    public CommunicationException(String message) {
        super(message);
        logger.error(message);
    }
}
