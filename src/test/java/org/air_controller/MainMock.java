package org.air_controller;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.MockGpioPin;
import org.air_controller.gpio.raspberry.GpioFunction;
import org.air_controller.gpio.raspberry.RaspberryGpioPin;
import org.air_controller.gpio.raspberry.RaspberryPin;

import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;

class MainMock {

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final RaspberryPin humidityExchangerPin = mock(RaspberryPin.class);
        final GpioPin humidityExchanger = new RaspberryGpioPin(GpioFunction.HUMIDITY_EXCHANGER.name(), humidityExchangerPin, true);
        final Application application = new Application(airFlow, humidityExchanger);
        application.run();
        Thread.currentThread().join();
    }
}