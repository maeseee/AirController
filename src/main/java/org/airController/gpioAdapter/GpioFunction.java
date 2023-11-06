package org.airController.gpioAdapter;

public enum GpioFunction {
    DHT22_SENSOR(7),         // BCM 4,  PIN 7
    MAIN_SYSTEM(21),         // BCM 5,  PIN 29
    HUMIDITY_EXCHANGER(22),  // BCM 6,  PIN 31
    UNMAPPED(23),            // BCM 13, PIN 33
    NIGHT_TIME(24);          // BCM 19, PIN 35

    private final int gpio;

    GpioFunction(int gpio) {
        this.gpio = gpio;
    }

    public int getGpio() {
        return gpio;
    }
}
