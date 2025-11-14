package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.ClimateDataPoint;
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
class QingPingSensorTest {
    @Mock
    private AccessToken accessToken;
    @Mock
    private ListDevicesRequest listDevicesRequest;
    @Mock
    private ListDevicesJsonParser parser;
    @Mock
    private ClimateDataPoint dataPoint;

    @Test
    void shouldParseSensorList() throws CommunicationException, IOException, URISyntaxException {
        final String token = "token";
        when(accessToken.readToken()).thenReturn(token);
        final String response = "response";
        when(listDevicesRequest.sendRequest(token)).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.of(dataPoint));
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevicesRequest, parser);

        final List<ClimateDataPoint> dataPoints = testee.readData();

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_2);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_3);
        assertThat(dataPoints).hasSize(4);
    }

    @Test
    void shouldIgnoreUnknownSensors() throws CommunicationException, IOException, URISyntaxException {
        final String token = "token";
        when(accessToken.readToken()).thenReturn(token);
        final String response = "response";
        when(listDevicesRequest.sendRequest(token)).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.empty());
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevicesRequest, parser);

        final List<ClimateDataPoint> dataPoints = testee.readData();

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE_1);
        assertThat(dataPoints).isEmpty();
    }
}