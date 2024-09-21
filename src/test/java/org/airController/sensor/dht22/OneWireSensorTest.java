package org.airController.sensor.dht22;

import org.airController.controllers.SensorData;
import org.airController.entities.Humidity;
import org.airController.entities.InvaildArgumentException;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneWireSensorTest {

    @Captor
    ArgumentCaptor<SensorData> indoorSensorDataArgumentCaptor;

    @Test
    void testWhenRunThenNotifyObservers() throws InvaildArgumentException {
        final Dht22 dht22 = mock(Dht22.class);
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final SensorData indoorSensorData = new Dht22SensorData(temperature, Humidity.createFromRelative(50.0, temperature));
        when(dht22.refreshData()).thenReturn(Optional.of(indoorSensorData));
        final OneWireSensor testee = new OneWireSensor(dht22);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorSensorData(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        assertEquals(indoorSensorData, indoorSensorDataCapture);
    }

    @Test
    void testWhenInvalidSensorDataThenDoNotNotifyObservers() {
        final Dht22 dht22 = mock(Dht22.class);
        when(dht22.refreshData()).thenReturn(Optional.empty());
        final OneWireSensor testee = new OneWireSensor(dht22);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verifyNoInteractions(observer);
    }

}