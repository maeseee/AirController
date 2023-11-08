package org.airController;

import org.airController.entities.AirVO;
import org.airController.gpio.GpioPinSpy;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.Dht22;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MainMock {

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        final GpioPin airFlow = new GpioPinSpy("AirFlow");
        final GpioPin humidityExchanger = new GpioPinSpy("HumidityExchanger");
        final Dht22 dht22 = mock(Dht22.class);
        final Optional<AirVO> airVo = Optional.of(new AirVO(23.0, 50.0));
        when(dht22.refreshData()).thenReturn(airVo);
        final Application application = new Application(airFlow, humidityExchanger, dht22);
        application.run();
        Thread.currentThread().join();
    }
}