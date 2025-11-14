package org.air_controller.sensor.qing_ping;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class QingPingSensor {

    private final AccessToken accessToken;
    private final ListDevicesRequest listDevicesRequest;

    public QingPingSensor() throws URISyntaxException {
        this(new AccessToken(), createListDevicesRequest());
    }

    QingPingSensor(AccessToken accessToken, ListDevicesRequest listDevicesRequest) {
        this.accessToken = accessToken;
        this.listDevicesRequest = listDevicesRequest;
    }

    public String readData() throws CommunicationException, IOException, URISyntaxException {
        final String token = accessToken.readToken();
        return listDevicesRequest.sendRequest(token);
    }

    public static List<String> getDeviceList() {
        return Devices.getDeviceList();
    }

    private static ListDevicesRequest createListDevicesRequest() throws URISyntaxException {
        final String urlString = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(urlString);
        return new ListDevicesRequest(uri);
    }
}
