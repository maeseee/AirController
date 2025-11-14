package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
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
    private ClimateDataPointPersistence persistence;

    @Captor
    private ArgumentCaptor<ClimateDataPoint> indoorDataPointArgumentCaptor;

    @Test
    void shouldPersistDataPoint_whenRun() throws InvalidArgumentException, CommunicationException, IOException, URISyntaxException {
        final AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final ListDevices listDevices = mock(ListDevices.class);
        final ClimateDataPoint dataPoint = new DataPointBuilder()
                .setTemperatureCelsius(21.5)
                .setHumidityAbsolute(10.0)
                .build();
        when(listDevices.readDataPoint(any())).thenReturn(List.of(dataPoint));
        final QingPingSensor testee = new QingPingSensor(persistence, accessToken, listDevices);

        testee.run();

        verify(persistence).persist(indoorDataPointArgumentCaptor.capture());
        final ClimateDataPoint indoorClimateDataPointCapture = indoorDataPointArgumentCaptor.getValue();
        assertThat(indoorClimateDataPointCapture.temperature()).isEqualTo(Temperature.createFromCelsius(21.5));
        assertThat(indoorClimateDataPointCapture.humidity()).isEqualTo(Humidity.createFromAbsolute(10.0));
    }

    @Test
    void shouldNotPersistDataPoint_whenInvalidDataPoint() throws CommunicationException, IOException, URISyntaxException {
        final AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final ListDevices listDevices = mock(ListDevices.class);
        when(listDevices.readDataPoint(any())).thenReturn(new ArrayList<>());
        final QingPingSensor testee = new QingPingSensor(persistence, accessToken, listDevices);

        testee.run();

        verifyNoInteractions(persistence);
    }
}