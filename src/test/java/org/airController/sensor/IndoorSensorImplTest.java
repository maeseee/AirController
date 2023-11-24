package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndoorSensorImplTest {

    @Captor
    ArgumentCaptor<AirValue> indoorAirValueArgumentCaptor;

    @Test
    void testWhenRunThenNotifyObservers() throws IOException {
        final Dht22 dht22 = mock(Dht22.class);
        final AirValue indoorAirValue = new AirValue(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(50.0));
        when(dht22.refreshData()).thenReturn(Optional.of(indoorAirValue));
        final IndoorSensorImpl testee = new IndoorSensorImpl(dht22);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorSensorValue(indoorAirValueArgumentCaptor.capture());
        final AirValue indoorAirValueCapture = indoorAirValueArgumentCaptor.getValue();
        assertEquals(indoorAirValue, indoorAirValueCapture);
    }

    @Test
    void testWhenInvalidSensorDataThenDoNotNotifyObservers() {
        final Dht22 dht22 = mock(Dht22.class);
        when(dht22.refreshData()).thenReturn(Optional.empty());
        final IndoorSensorImpl testee = new IndoorSensorImpl(dht22);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verifyNoInteractions(observer);
    }

}