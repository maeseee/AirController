package org.airController;

import org.airController.entities.AirVO;
import org.airController.gpio.GpioPinMock;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.Dht22;
import org.airController.sensor.IndoorSensorImpl;
import org.airController.sensor.SensorValueImpl;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.SensorValue;

import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MainMock {

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        final GpioPin airFlow = new GpioPinMock("AirFlow");
        final GpioPin humidityExchanger = new GpioPinMock("HumidityExchanger");
        final AirVO airVo = new AirVO(23.0, 50.0);
        final SensorValue sensorValue = new SensorValueImpl(airVo);
        final Dht22 dht22 = mock(Dht22.class);
        when(dht22.refreshData()).thenReturn(sensorValue);
        final IndoorSensor indoorSensor = new IndoorSensorImpl(dht22);
        final Application application = new Application(airFlow, humidityExchanger, indoorSensor);
        application.run();
        Thread.currentThread().join();
    }
}