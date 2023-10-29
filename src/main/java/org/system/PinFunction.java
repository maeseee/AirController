package org.system;

public enum PinFunction {
    DHT22_SENSOR(4),
    MAIN_SYSTEM(5),
    HUMIDITY_CONTROL(6),
    UNMAPPED(13),
    NIGHT_TIME(19);

    private final int pin;

    private PinFunction(int pin) {
        this.pin = pin;
    }

    public int getPin() {
        return pin;
    }
}
