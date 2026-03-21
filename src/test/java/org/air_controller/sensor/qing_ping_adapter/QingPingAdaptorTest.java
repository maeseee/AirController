package org.air_controller.sensor.qing_ping_adapter;

import org.air_controller.ControlledTask;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class QingPingAdaptorTest {

    @MockitoBean(name = "indoorPersistence")
    private ClimateDataPointPersistence persistence;
    @MockitoBean
    private QingPingSensor sensor;
    @MockitoBean
    private SensorReducer reducer;
    @MockitoBean
    private ListDevicesJsonParser parser;
    @Autowired
    private ControlledTask task;

    @Captor
    private ArgumentCaptor<ClimateDataPoint> indoorDataPointArgumentCaptor;

    @Test
    void shouldPersistDataPoint_whenRun() throws InvalidArgumentException {
        final ClimateDataPoint dataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(23.0)
                .setHumidityAbsolute(10.0)
                .build();
        when(sensor.readData()).thenReturn("Response");
        when(parser.parseDeviceListResponse(any(), any())).thenReturn(Optional.of(dataPoint));
        when(reducer.reduce(any())).thenReturn(Optional.of(dataPoint));

        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor, task, reducer, parser);

        testee.runAtTenMinuteIntervals();

        verify(sensor).readData();
        verify(persistence).persist(indoorDataPointArgumentCaptor.capture());
        final ClimateDataPoint indoorClimateDataPointCapture = indoorDataPointArgumentCaptor.getValue();
        assertThat(indoorClimateDataPointCapture.temperature()).isEqualTo(Temperature.createFromCelsius(23.0));
        assertThat(indoorClimateDataPointCapture.humidity()).isEqualTo(Humidity.createFromAbsolute(10.0));
    }

    @Test
    void shouldNotPersistDataPoint_whenInvalidDataPoint() {
        when(sensor.readData()).thenReturn("");
        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor, task);

        testee.runAtTenMinuteIntervals();

        verifyNoInteractions(persistence);
    }
}