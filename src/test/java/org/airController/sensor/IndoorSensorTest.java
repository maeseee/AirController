package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.SensorValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndoorSensorTest {

    @Captor
    ArgumentCaptor<SensorValue> indoorSensorValueArgumentCaptor;

    @Test
    void testWhenRunThenNotifyObservers() {
        final Dht22 dht22 = mock(Dht22.class);
        final AirVO data = new AirVO(23.0, 50.0);
        when(dht22.refreshData()).thenReturn(new SensorValueImpl(data));
        final IndoorSensor testee = new IndoorSensor(dht22);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorSensorValue(indoorSensorValueArgumentCaptor.capture());
        final SensorValue indoorSensorValue = indoorSensorValueArgumentCaptor.getValue();
        final Optional<AirVO> sensorValue = indoorSensorValue.getValue();
        assertTrue(sensorValue.isPresent());
        assertEquals(sensorValue.get(), data);
    }

    @Test
    void testWhenInvalidSensorDataThenDoNotNotifyObservers() {
        final Dht22 dht22 = mock(Dht22.class);
        when(dht22.refreshData()).thenReturn(new SensorValueImpl(null));
        final IndoorSensor testee = new IndoorSensor(dht22);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verifyNoInteractions(observer);
    }

}