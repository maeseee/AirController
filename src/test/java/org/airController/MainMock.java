package org.airController;

import org.airController.gpio.GpioPinImpl;
import org.airController.gpio.MockGpioPin;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.OutdoorSensorImpl;
import org.airController.sensor.QingPingSensor;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.OutdoorSensor;
import org.airController.util.RaspberryPiPin;

import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;

class MainMock {

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final RaspberryPiPin humidityExchangerPin = mock(RaspberryPiPin.class);
        final GpioPin humidityExchanger = new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER.name(), humidityExchangerPin, true);
        final OutdoorSensor outdoorSensor = new OutdoorSensorImpl();
        final IndoorSensor indoorSensor = new QingPingSensor();
        final Application application = new Application(airFlow, humidityExchanger, outdoorSensor, indoorSensor, Executors.newScheduledThreadPool(1));
        application.run();
        Thread.currentThread().join();
    }
}