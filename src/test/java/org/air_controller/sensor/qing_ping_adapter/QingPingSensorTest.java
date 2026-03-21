package org.air_controller.sensor.qing_ping_adapter;

import org.air_controller.ControlledTask;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.air_controller.sensor.qing_ping.Devices.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class QingPingSensorTest {

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

    @Test
    void shouldParseSensorList() throws InvalidArgumentException {
        final String response = "response";
        when(sensor.readData()).thenReturn(response);
        final ClimateDataPoint dataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(21.0)
                .setHumidityRelative(40.0)
                .build();
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.of(dataPoint));
        when(reducer.reduce(any())).thenReturn(Optional.of(dataPoint));
        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor, task, reducer, parser);

        testee.runAtTenMinuteIntervals();

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_2);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_3);
        verify(persistence).persist(eq(dataPoint));
    }

    @Test
    void shouldIgnoreUnknownSensors() {
        final String response = "response";
        when(sensor.readData()).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.empty());
        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor, task, new SensorReducer(), parser);

        testee.runAtTenMinuteIntervals();

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_2);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_3);
        verifyNoInteractions(persistence);
    }
}