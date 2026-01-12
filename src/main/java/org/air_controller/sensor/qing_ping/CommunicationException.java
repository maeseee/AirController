package org.air_controller.sensor.qing_ping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommunicationException extends Exception {
    public CommunicationException(String message) {
        super(message);
        log.error(message);
    }
}
