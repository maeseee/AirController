package org.airController.sensor.qingPing;

import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvaildArgumentException;
import org.airController.sensorValues.Temperature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.airController.sensor.qingPing.QingPingDevices.MAC_AIR_PRESSURE_DEVICE;
import static org.airController.sensor.qingPing.QingPingDevices.MAC_CO2_DEVICE;
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

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE);
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

        verify(parser).parseDeviceListResponse(response, MAC_AIR_PRESSURE_DEVICE);
        verify(parser).parseDeviceListResponse(response, MAC_CO2_DEVICE);
        assertThat(sensorDataList).hasSize(0);
    }

    @Test
    void shouldCorrectHumidity_whenCo2Sensor() throws CommunicationException, IOException, URISyntaxException, InvaildArgumentException {
        final String token = "token";
        final String response = "response";
        when(listDevicesRequest.sendRequest(token)).thenReturn(response);
        final LocalDateTime now = LocalDateTime.now();
        final QingPingSensorData sensorData = createSensorData(10.0, now);
        when(parser.parseDeviceListResponse(eq(response), eq(MAC_AIR_PRESSURE_DEVICE))).thenReturn(Optional.of(sensorData));
        when(parser.parseDeviceListResponse(eq(response), eq(MAC_CO2_DEVICE))).thenReturn(Optional.of(sensorData));
        final QingPingListDevices testee = new QingPingListDevices(listDevicesRequest, parser);

        final List<QingPingSensorData> sensorDataList = testee.readSensorDataList(token);

        assertThat(sensorDataList).hasSize(2);
        assertThat(sensorDataList).contains(sensorData);
        final QingPingSensorData sensorDataCo2Device = createSensorData(9.26, now);
        assertThat(sensorDataList).contains(sensorDataCo2Device);
    }

    private QingPingSensorData createSensorData(double absoluteHumidity, LocalDateTime timestamp) throws InvaildArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(22.0);
        final Humidity humidity = Humidity.createFromAbsolute(absoluteHumidity);
        return new QingPingSensorData(temperature, humidity, null, timestamp);
    }
}