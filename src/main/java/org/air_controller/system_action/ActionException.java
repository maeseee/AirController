package org.air_controller.system_action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionException extends RuntimeException {
    private static final Logger logger = LogManager.getLogger(ActionException.class);

    public ActionException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
