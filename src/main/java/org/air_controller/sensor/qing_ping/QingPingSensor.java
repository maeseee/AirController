package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class QingPingSensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final AccessToken accessToken;
    private final ListDevicesRequest listDevicesRequest;
    private final ListDevicesJsonParser parser;

    public QingPingSensor() throws URISyntaxException {
        this(new AccessToken(), createListDevicesRequest(), new ListDevicesJsonParser());
    }

    QingPingSensor(AccessToken accessToken, ListDevicesRequest listDevicesRequest, ListDevicesJsonParser parser) {
        this.accessToken = accessToken;
        this.listDevicesRequest = listDevicesRequest;
        this.parser = parser;
    }

    public List<ClimateDataPoint> readData() throws CommunicationException, IOException, URISyntaxException {
        final String token = accessToken.readToken();
        final String response = listDevicesRequest.sendRequest(token);
        final List<ClimateDataPoint> climateDataPoints = new ArrayList<>();
        Devices.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac).ifPresent(climateDataPoints::add));
        if (climateDataPoints.isEmpty()) {
            logger.error("No sensor data found in the response: {}", response);
        }
        return climateDataPoints;
    }

    private static ListDevicesRequest createListDevicesRequest() throws URISyntaxException {
        final String urlString = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(urlString);
        return new ListDevicesRequest(uri);
    }
}
