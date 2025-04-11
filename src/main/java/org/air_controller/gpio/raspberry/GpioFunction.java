package org.air_controller.gpio.raspberry;

import lombok.Getter;

@Getter
public enum GpioFunction {
    DHT22_SENSOR(7, 4),        // PIN 7
    AIR_FLOW(21, 5),           // PIN 29
    HUMIDITY_EXCHANGER(22, 6), // PIN 31
    UNMAPPED(23, 13),          // PIN 33
    NIGHT_TIME(24, 19);        // PIN 35

    private final int gpio;
    private final int bcm;

    GpioFunction(int gpio, int bcm) {
        this.gpio = gpio;
        this.bcm = bcm;
    }

}
