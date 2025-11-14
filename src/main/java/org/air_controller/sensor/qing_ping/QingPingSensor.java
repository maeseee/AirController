package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class QingPingSensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final AccessToken accessToken;
    private final ListDevices listDevices;
    private final SensorReducer sensorReducer = new SensorReducer();

    public QingPingSensor() throws URISyntaxException {
        this(new AccessToken(), new ListDevices());
    }

    QingPingSensor(AccessToken accessToken,
            ListDevices listDevices) {
        this.accessToken = accessToken;
        this.listDevices = listDevices;
    }

    public ClimateDataPoint readData() throws CommunicationException, IOException, URISyntaxException, InvalidArgumentException, CalculationException {
        final String token = accessToken.readToken();
        final List<ClimateDataPoint> climateDataPoints = listDevices.readDataPoint(token);
        return sensorReducer.reduce(climateDataPoints);
    }
}
