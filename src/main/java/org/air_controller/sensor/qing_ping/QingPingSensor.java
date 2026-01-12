package org.air_controller.sensor.qing_ping;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor.SensorReader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class QingPingSensor implements SensorReader {
    private final AccessToken accessToken;
    private final ListDevicesRequest listDevicesRequest;

    public QingPingSensor() throws URISyntaxException {
        this(new AccessToken(), createListDevicesRequest());
    }

    QingPingSensor(AccessToken accessToken, ListDevicesRequest listDevicesRequest) {
        this.accessToken = accessToken;
        this.listDevicesRequest = listDevicesRequest;
    }

    public String readData() {
        try {
            final String token = accessToken.readToken();
            return listDevicesRequest.sendRequest(token);
        } catch (CommunicationException | IOException | URISyntaxException e) {
            log.error("QingPingSensor readData error", e);
            return "";
        }
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
