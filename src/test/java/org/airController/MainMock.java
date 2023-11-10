package org.airController;

import org.airController.entities.AirVO;
import org.airController.gpio.GpioPinSpy;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.Dht22Mock;
import org.airController.sensor.IndoorAirMeasurement;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;

class MainMock {

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        final GpioPin airFlow = new GpioPinSpy("AirFlow");
        final GpioPin humidityExchanger = new GpioPinSpy("HumidityExchanger");
        final Dht22Mock dht22Mock = new Dht22Mock();
        final AirVO airVo = new AirVO(23.0, 50.0);
        dht22Mock.setData(airVo);
        final IndoorAirMeasurement indoorAirMeasurement = new IndoorAirMeasurement(dht22Mock);
        final Application application = new Application(airFlow, humidityExchanger, indoorAirMeasurement);
        application.run();
        Thread.currentThread().join();
    }
}