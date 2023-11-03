package org.airController;

import org.airController.gpio.GpioPinSpy;
import org.airController.gpioAdapter.GpioPin;

import java.io.IOException;
import java.net.URISyntaxException;

class MainMock {

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        final GpioPin airFlow = new GpioPinSpy("AirFlow");
        final GpioPin humidityExchanger = new GpioPinSpy("HumidityExchanger");
        final Application application = new Application(airFlow, humidityExchanger);
        application.init();
        application.run();
        Thread.currentThread().join();
    }
}