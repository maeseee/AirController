package org.air_controller.gpio.dingtian_relay.raspberry;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.raspberry.GpioFunction;
import org.air_controller.gpio.raspberry.RaspberryGpioPin;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RaspberryPiPinMain {
    public static void main(String[] args) throws IOException {
        final GpioPin gpioPin = new RaspberryGpioPin(GpioFunction.AIR_FLOW, true);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> gpioPin.setGpioState(!gpioPin.getGpioState()), 0, 2, TimeUnit.SECONDS);
    }
}
