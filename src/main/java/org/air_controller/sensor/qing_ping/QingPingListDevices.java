package org.air_controller.sensor.qing_ping;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

class QingPingListDevices {
    private final QingPingListDevicesRequest listDevicesRequest;
    private final QingPingListDevicesJsonParser parser;

    public QingPingListDevices() throws URISyntaxException {
        this(createListDevicesRequest(), new QingPingListDevicesJsonParser());
    }

    QingPingListDevices(QingPingListDevicesRequest listDevicesRequest, QingPingListDevicesJsonParser parser) {
        this.listDevicesRequest = listDevicesRequest;
        this.parser = parser;
    }

    public List<QingPingSensorData> readSensorDataList(String token) throws CommunicationException, IOException, URISyntaxException {
        final String response = listDevicesRequest.sendRequest(token);
        final List<QingPingSensorData> sensorDataList = new ArrayList<>();
        QingPingDevices.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac).ifPresent(sensorDataList::add));
        return sensorDataList;
    }


    private static QingPingListDevicesRequest createListDevicesRequest() throws URISyntaxException {
        final String urlString = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(urlString);
        return new QingPingListDevicesRequest(uri);
    }
}
