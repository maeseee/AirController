package org.airController;

import org.airController.gpio.*;

import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;

class MainMock {

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final RaspberryPiPin humidityExchangerPin = mock(RaspberryPiPin.class);
        final GpioPin humidityExchanger = new RaspberryGpioPin(GpioFunction.HUMIDITY_EXCHANGER.name(), humidityExchangerPin, true);
        final Application application = new Application(airFlow, humidityExchanger);
        application.run();
        Thread.currentThread().join();
    }
}