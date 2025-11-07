package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.air_controller.sensor_values.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QingPingSensorTest {

    @Mock
    private SensorDataPersistence persistence;

    @Captor
    private ArgumentCaptor<SensorData> indoorSensorDataArgumentCaptor;

    @Test
    void shouldNotifyObservers_whenRun() throws InvalidArgumentException, CommunicationException, IOException, URISyntaxException {
        final AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final ListDevices listDevices = mock(ListDevices.class);
        final SensorData sensorData = new SensorDataBuilder()
                .setTemperatureCelsius(21.5)
                .setHumidityAbsolute(10.0)
                .build();
        when(listDevices.readSensorDataList(any())).thenReturn(List.of(sensorData));
        final QingPingSensor testee = new QingPingSensor(persistence, accessToken, listDevices);

        testee.run();

        verify(persistence).persist(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        assertThat(indoorSensorDataCapture.temperature()).isEqualTo(Temperature.createFromCelsius(21.5));
        assertThat(indoorSensorDataCapture.humidity()).isEqualTo(Humidity.createFromAbsolute(10.0));
    }

    @Test
    void shouldNotNotifyObservers_whenInvalidSensorData() throws CommunicationException, IOException, URISyntaxException {
        final AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final ListDevices listDevices = mock(ListDevices.class);
        when(listDevices.readSensorDataList(any())).thenReturn(new ArrayList<>());
        final QingPingSensor testee = new QingPingSensor(persistence, accessToken, listDevices);

        testee.run();

        verifyNoInteractions(persistence);
    }
}