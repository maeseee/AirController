package org.air_controller.gpio;

public interface GpioPin {

    void setGpioState(boolean stateOn);

    boolean getGpioState();
}
