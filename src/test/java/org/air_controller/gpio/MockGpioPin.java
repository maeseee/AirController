package org.air_controller.gpio;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockGpioPin implements GpioPin {
    private final String name;
    private boolean gpioState;

    public MockGpioPin(String name, boolean initialHigh) {
        this.name = name;
        gpioState = initialHigh;
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            log.info("{} set to {}", name, stateOn ? "on" : "off");
            gpioState = stateOn;
        }
    }

    @Override
    public boolean getGpioState() {
        return gpioState;
    }
}
