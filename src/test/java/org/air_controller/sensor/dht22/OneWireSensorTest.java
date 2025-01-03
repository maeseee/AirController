package org.air_controller.sensor.dht22;

import org.air_controller.sensorValues.Humidity;
import org.air_controller.sensorValues.InvalidArgumentException;
import org.air_controller.sensorValues.SensorData;
import org.air_controller.sensorValues.Temperature;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneWireSensorTest {

    @Mock
    private SensorDataPersistence persistence;

    @Captor
    private ArgumentCaptor<SensorData> indoorSensorDataArgumentCaptor;

    @Test
    void testWhenRunThenPersistData() throws InvalidArgumentException {
        final Dht22 dht22 = mock(Dht22.class);
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final SensorData indoorSensorData = new Dht22SensorData(temperature, Humidity.createFromRelative(50.0, temperature));
        when(dht22.refreshData()).thenReturn(Optional.of(indoorSensorData));
        final OneWireSensor testee = new OneWireSensor(persistence, dht22);

        testee.run();

        verify(persistence).persist(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        assertEquals(indoorSensorData, indoorSensorDataCapture);
    }

    @Test
    void testWhenInvalidSensorDataThenDoNotPersistData() {
        final Dht22 dht22 = mock(Dht22.class);
        when(dht22.refreshData()).thenReturn(Optional.empty());
        final OneWireSensor testee = new OneWireSensor(persistence, dht22);

        testee.run();

        verifyNoInteractions(persistence);
    }

}