package org.air_controller.sensor.qing_ping;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

class ListDevices {
    private final ListDevicesRequest listDevicesRequest;
    private final ListDevicesJsonParser parser;

    public ListDevices() throws URISyntaxException {
        this(createListDevicesRequest(), new ListDevicesJsonParser());
    }

    ListDevices(ListDevicesRequest listDevicesRequest, ListDevicesJsonParser parser) {
        this.listDevicesRequest = listDevicesRequest;
        this.parser = parser;
    }

    public List<HwSensorData> readSensorDataList(String token) throws CommunicationException, IOException, URISyntaxException {
        final String response = listDevicesRequest.sendRequest(token);
        final List<HwSensorData> sensorDataList = new ArrayList<>();
        Devices.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac).ifPresent(sensorDataList::add));
        return sensorDataList;
    }


    private static ListDevicesRequest createListDevicesRequest() throws URISyntaxException {
        final String urlString = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(urlString);
        return new ListDevicesRequest(uri);
    }
}
