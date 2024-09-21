package org.airController.entities;

public class InvaildArgumentException extends Exception {
    public InvaildArgumentException(String message) {
        super(message);
    }

    public InvaildArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
