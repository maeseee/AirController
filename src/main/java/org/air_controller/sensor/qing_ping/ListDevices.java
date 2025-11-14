package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

class ListDevices {
    private static final Logger logger = LogManager.getLogger(ListDevices.class);

    private final ListDevicesRequest listDevicesRequest;
    private final ListDevicesJsonParser parser;

    public ListDevices() throws URISyntaxException {
        this(createListDevicesRequest(), new ListDevicesJsonParser());
    }

    ListDevices(ListDevicesRequest listDevicesRequest, ListDevicesJsonParser parser) {
        this.listDevicesRequest = listDevicesRequest;
        this.parser = parser;
    }

    public List<ClimateDataPoint> readDataPoint(String token) throws CommunicationException, IOException, URISyntaxException {
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
