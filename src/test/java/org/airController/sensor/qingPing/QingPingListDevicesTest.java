package org.airController.sensor.qingPing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QingPingListDevicesTest {
    @Mock
    private QingPingListDevicesRequest listDevicesRequest;
    @Mock
    private QingPingListDevicesJsonParser parser;
    @Mock
    private QingPingSensorData sensorData;

    @Test
    void shouldParseSensorList() throws CommunicationException, IOException, URISyntaxException {
        final String token = "token";
        final String response = "response";
        when(listDevicesRequest.sendRequest(token)).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.of(sensorData));
        final QingPingListDevices testee = new QingPingListDevices(listDevicesRequest, parser);

        final List<QingPingSensorData> sensorDataList = testee.readSensorDataList(token);

        verify(parser).parseDeviceListResponse(response, QingPingDevices.getAirPressureDevices().get(0));
        verify(parser).parseDeviceListResponse(response, QingPingDevices.getCo2Devices().get(0));
        assertThat(sensorDataList).hasSize(2);
    }

    @Test
    void shouldIgnoreUnknownSensors() throws CommunicationException, IOException, URISyntaxException {
        final String token = "token";
        final String response = "response";
        when(listDevicesRequest.sendRequest(token)).thenReturn(response);
        when(parser.parseDeviceListResponse(eq(response), any())).thenReturn(Optional.empty());
        final QingPingListDevices testee = new QingPingListDevices(listDevicesRequest, parser);

        final List<QingPingSensorData> sensorDataList = testee.readSensorDataList(token);

        verify(parser).parseDeviceListResponse(response, QingPingDevices.getAirPressureDevices().get(0));
        verify(parser).parseDeviceListResponse(response, QingPingDevices.getCo2Devices().get(0));
        assertThat(sensorDataList).hasSize(0);
    }
}