package org.airController.util;

import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class RaspberryPiPinTest {

    public static void main(String[] args) throws IOException {
        final GpioPinImpl gpioPin = new GpioPinImpl(GpioFunction.AIR_FLOW, true);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> gpioPin.setGpioState(!gpioPin.getGpioState()), 0, 2, TimeUnit.SECONDS);
    }

}