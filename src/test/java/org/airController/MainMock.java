package org.airController;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.Dht22;
import org.airController.sensor.IndoorSensorImpl;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.util.RaspberryPiPin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MainMock {

    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        final RaspberryPiPin airFlowPin = mock(RaspberryPiPin.class);
        final GpioPin airFlow = new GpioPinImpl(GpioFunction.AIR_FLOW.name(), airFlowPin, true);
        final RaspberryPiPin humidityExchangerPin = mock(RaspberryPiPin.class);
        final GpioPin humidityExchanger = new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER.name(), humidityExchangerPin, true);
        final AirValue airValue = new AirValue(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(50.0));
        final Dht22 dht22 = mock(Dht22.class);
        when(dht22.refreshData()).thenReturn(Optional.of(airValue));
        final IndoorSensor indoorSensor = new IndoorSensorImpl(dht22);
        final Application application = new Application(airFlow, humidityExchanger, indoorSensor);
        application.run();
        Thread.currentThread().join();
    }
}