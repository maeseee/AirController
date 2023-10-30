package org.gpio;

public enum GpioFunction {
    DHT22_SENSOR(4),
    MAIN_SYSTEM(5),
    HUMIDITY_EXCHANGER(6),
    UNMAPPED(13),
    NIGHT_TIME(19);

    private final int pin;

    GpioFunction(int pin) {
        this.pin = pin;
    }

    public int getPin() {
        return pin;
    }
}
