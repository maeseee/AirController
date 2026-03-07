package org.air_controller.secrets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ParseException extends RuntimeException {
    public ParseException(String message, Throwable cause) {
        super(message, cause);
        log.error(message);
    }
}
