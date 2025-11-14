package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class QingPingSensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final AccessToken accessToken;
    private final ListDevices listDevices;

    public QingPingSensor() throws URISyntaxException {
        this(new AccessToken(), new ListDevices());
    }

    QingPingSensor(AccessToken accessToken,
            ListDevices listDevices) {
        this.accessToken = accessToken;
        this.listDevices = listDevices;
    }

    public List<ClimateDataPoint> readData() throws CommunicationException, IOException, URISyntaxException {
        final String token = accessToken.readToken();
        return listDevices.readDataPoint(token);
    }
}
