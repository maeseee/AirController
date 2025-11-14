package org.air_controller.sensor.ping_ping_adapter;

import org.air_controller.sensor.qing_ping.CommunicationException;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.DataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.air_controller.sensor.qing_ping.Devices.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QingPingSensorTest {

    @Mock
    private ClimateDataPointPersistence persistence;
    @Mock
    private QingPingSensor sensor;
    @Mock
    private SensorReducer reducer;
    @Mock
    private ListDevicesJsonParser parser;

    @Test
    void shouldParseSensorList() throws CommunicationException, IOException, URISyntaxException, InvalidArgumentException, CalculationException {
        final String response = "response";
        when(sensor.readData()).thenReturn(response);
        final ClimateDataPoint dataPoint = new DataPointBuilder()
                .setTemperatureCelsius(21.0)
                .setHumidityRelative(40.0)
                .build();
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.of(dataPoint));
        when(reducer.reduce(any())).thenReturn(dataPoint);
        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor, reducer, parser);

        testee.run();

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_2);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_3);
        verify(persistence).persist(eq(dataPoint));
    }

    @Test
    void shouldIgnoreUnknownSensors() throws CommunicationException, IOException, URISyntaxException {
        final String response = "response";
        when(sensor.readData()).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.empty());
        final QingPingAdapter testee = new QingPingAdapter(persistence, sensor, new SensorReducer(), parser);

        testee.run();

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_2);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_3);
        verifyNoInteractions(persistence);
    }
}