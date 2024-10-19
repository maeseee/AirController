package org.airController.sensor.qingPing;

import org.airController.sensorDataPersistence.SensorDataPersistence;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.SensorData;
import org.airController.sensorValues.Temperature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
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
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final QingPingListDevices listDevices = mock(QingPingListDevices.class);
        final Temperature temperature = Temperature.createFromCelsius(21.5);
        final Humidity humidity = Humidity.createFromAbsolute(10.0);
        final LocalDateTime time1 = LocalDateTime.now();
        final QingPingSensorData sensorData = new QingPingSensorData(temperature, humidity, null, time1);
        when(listDevices.readSensorDataList(any())).thenReturn(List.of(sensorData));
        final QingPingSensor testee = new QingPingSensor(persistence, null, accessToken, listDevices);

        testee.run();

        verify(persistence).persist(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        assertThat(indoorSensorDataCapture.getTemperature()).isPresent().hasValue(Temperature.createFromCelsius(21.5));
        assertThat(indoorSensorDataCapture.getHumidity()).isPresent().hasValue(Humidity.createFromAbsolute(10.0));
    }

    @Test
    void shouldNotNotifyObservers_whenInvalidSensorData() throws CommunicationException, IOException, URISyntaxException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final QingPingListDevices listDevices = mock(QingPingListDevices.class);
        when(listDevices.readSensorDataList(any())).thenReturn(new ArrayList<>());
        final QingPingSensor testee = new QingPingSensor(persistence, null, accessToken, listDevices);

        testee.run();

        verifyNoInteractions(persistence);
    }
}