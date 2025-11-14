package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor.ping_ping_adapter.QingPingAdapter;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QingPingAdaptorTest {

    @Mock
    private ClimateDataPointPersistence persistence;
    @Mock
    private QingPingSensor sensor;

    @Captor
    private ArgumentCaptor<ClimateDataPoint> indoorDataPointArgumentCaptor;

    @Test
    void shouldPersistDataPoint_whenRun()
            throws InvalidArgumentException, CommunicationException, IOException, URISyntaxException, CalculationException {
        final ClimateDataPoint dataPoint = new DataPointBuilder()
                .setTemperatureCelsius(23.0)
                .setHumidityAbsolute(10.0)
                .build();
        when(sensor.readData()).thenReturn(dataPoint);
        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor);

        testee.run();

        verify(sensor).readData();
        verify(persistence).persist(indoorDataPointArgumentCaptor.capture());
        final ClimateDataPoint indoorClimateDataPointCapture = indoorDataPointArgumentCaptor.getValue();
        assertThat(indoorClimateDataPointCapture.temperature()).isEqualTo(Temperature.createFromCelsius(23.0));
        assertThat(indoorClimateDataPointCapture.humidity()).isEqualTo(Humidity.createFromAbsolute(10.0));
    }

    @Test
    void shouldNotPersistDataPoint_whenInvalidDataPoint()
            throws CommunicationException, IOException, URISyntaxException, InvalidArgumentException, CalculationException {
        when(sensor.readData()).thenThrow(new CalculationException("Error"));
        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor);

        testee.run();

        verifyNoInteractions(persistence);
    }
}