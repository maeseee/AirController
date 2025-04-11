package org.air_controller.gpio.raspberry;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

import java.io.IOException;

class GpioHwPin {

    private final RaspberryGpioFunction gpioFunction;

    public RaspberryPin(GpioFunction gpioFunction) throws IOException {
        this.gpioFunction = gpioFunction;
        setupWiringPi();
    }

    public void export(boolean directionOut) {
        final int direction = directionOut ? GpioUtil.DIRECTION_IN : GpioUtil.DIRECTION_OUT;
        GpioUtil.export(gpioFunction.getBcm(), direction);
    }

    public void write(boolean high) {
        final int state = high ? Gpio.HIGH : Gpio.LOW;
        Gpio.digitalWrite(gpioFunction.getGpio(), state);
    }

    public boolean read() {
        final int state = Gpio.digitalRead(gpioFunction.getGpio());
        return state != 0;
    }

    private void setupWiringPi() throws IOException {
        final int wiringPiStatus = com.pi4j.wiringpi.Gpio.wiringPiSetup();
        if (wiringPiStatus == -1) {
            throw new IOException("GPIO SETUP FAILED");
        }
    }
}
