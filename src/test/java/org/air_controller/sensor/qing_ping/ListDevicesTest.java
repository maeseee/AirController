package org.air_controller.sensor.qing_ping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.air_controller.sensor.qing_ping.Devices.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDevicesTest {
    @Mock
    private ListDevicesRequest listDevicesRequest;
    @Mock
    private ListDevicesJsonParser parser;
    @Mock
    private HwSensorData sensorData;

    @Test
    void shouldParseSensorList() throws CommunicationException, IOException, URISyntaxException {
        final String token = "token";
        final String response = "response";
        when(listDevicesRequest.sendRequest(token)).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.of(sensorData));
        final ListDevices testee = new ListDevices(listDevicesRequest, parser);

        final List<HwSensorData> sensorDataList = testee.readSensorDataList(token);

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_2);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_3);
        assertThat(sensorDataList).hasSize(4);
    }

    @Test
    void shouldIgnoreUnknownSensors() throws CommunicationException, IOException, URISyntaxException {
        final String token = "token";
        final String response = "response";
        when(listDevicesRequest.sendRequest(token)).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.empty());
        final ListDevices testee = new ListDevices(listDevicesRequest, parser);

        final List<HwSensorData> sensorDataList = testee.readSensorDataList(token);

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        assertThat(sensorDataList).isEmpty();
    }
}