package org.air_controller.sensor.qing_ping;

import lombok.Getter;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class QingPingSensor implements Sensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    @Getter
    private final ClimateDataPointPersistence persistence;
    private final AccessToken accessToken;
    private final ListDevices listDevices;
    private final SensorReducer sensorReducer = new SensorReducer();

    public QingPingSensor(ClimateDataPointPersistence persistence) throws URISyntaxException {
        this(persistence, new AccessToken(), new ListDevices());
    }

    QingPingSensor(ClimateDataPointPersistence persistence, AccessToken accessToken,
            ListDevices listDevices) {
        this.persistence = persistence;
        this.accessToken = accessToken;
        this.listDevices = listDevices;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            logger.error("Exception in QingPing sensor loop:", exception);
        }
    }

    private void doRun() throws CommunicationException, IOException, URISyntaxException, InvalidArgumentException, CalculationException {
        final String token = accessToken.readToken();
        final List<ClimateDataPoint> climateDataPoints = listDevices.readSensorDataList(token);
        final ClimateDataPoint dataPoint = sensorReducer.reduce(climateDataPoints);
        notifyObservers(dataPoint);
    }

    private void notifyObservers(ClimateDataPoint dataPoint) {
        logger.info("New indoor data point: {}", dataPoint);
        persistence.persist(dataPoint);
    }

}
