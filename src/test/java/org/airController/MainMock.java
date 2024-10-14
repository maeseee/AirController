package org.airController;

import org.airController.gpio.*;
import org.airController.persistence.SensorDataPersistenceObserver;
import org.airController.sensor.OutdoorSensor;
import org.airController.sensor.openWeatherApi.OpenWeatherApiSensor;
import org.airController.sensor.qingPing.QingPingSensor;

import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;

class MainMock {

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final RaspberryPiPin humidityExchangerPin = mock(RaspberryPiPin.class);
        final GpioPin humidityExchanger = new RaspberryGpioPin(GpioFunction.HUMIDITY_EXCHANGER.name(), humidityExchangerPin, true);
        final OutdoorSensor outdoorSensor = new OpenWeatherApiSensor();
        final QingPingSensor indoorSensor = new QingPingSensor();
        final SensorDataPersistenceObserver persistenceObserver = new SensorDataPersistenceObserver();
        final Application application = new Application(airFlow, humidityExchanger, outdoorSensor, indoorSensor, null, persistenceObserver);
        application.run();
        Thread.currentThread().join();
    }
}