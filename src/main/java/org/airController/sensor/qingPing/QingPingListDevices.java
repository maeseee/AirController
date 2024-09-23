package org.airController.sensor.qingPing;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QingPingListDevices {
    static final String MAC_PRESSURE_DEVICE = "582D3480A7F4";
    static final String MAC_CO2_DEVICE = "582D34831850";

    private final QingPingListDevicesRequest listDevicesRequest;
    private final QingPingListDevicesJsonParser parser;
    private final List<String> deviceMacAddresses;

    public QingPingListDevices() throws URISyntaxException {
        this(createListDevicesRequest(), new QingPingListDevicesJsonParser(), Arrays.asList(MAC_PRESSURE_DEVICE, MAC_CO2_DEVICE));
    }

    QingPingListDevices(QingPingListDevicesRequest listDevicesRequest, QingPingListDevicesJsonParser parser, List<String> deviceMacAddresses) {
        this.listDevicesRequest = listDevicesRequest;
        this.parser = parser;
        this.deviceMacAddresses = deviceMacAddresses;
    }

    public List<QingPingSensorData> readSensorDataList(String token) throws CommunicationException, IOException, URISyntaxException {
        final String response = listDevicesRequest.sendRequest(token);
        final List<QingPingSensorData> sensorDataList = new ArrayList<>();
        deviceMacAddresses.forEach(mac -> parser.parseDeviceListResponse(response, mac).ifPresent(sensorDataList::add));
        return sensorDataList;
    }

    private static QingPingListDevicesRequest createListDevicesRequest() throws URISyntaxException {
        final String urlString = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(urlString);
        return new QingPingListDevicesRequest(uri);
    }
}
