package org.air_controller.sensor.qing_ping;

import lombok.Getter;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.SensorData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class QingPingSensor implements Sensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    @Getter
    private final SensorDataPersistence persistence;
    private final AccessToken accessToken;
    private final ListDevices listDevices;
    private final SensorReducer sensorReducer = new SensorReducer();

    public QingPingSensor(SensorDataPersistence persistence) throws URISyntaxException {
        this(persistence, new AccessToken(), new ListDevices());
    }

    QingPingSensor(SensorDataPersistence persistence, AccessToken accessToken,
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
        final List<HwSensorData> sensorDataList = listDevices.readSensorDataList(token);
        final SensorData sensorData = sensorReducer.reduce(sensorDataList);
        notifyObservers(sensorData);
    }

    private void notifyObservers(SensorData sensorData) {
        logger.info("New indoor sensor data: {}", sensorData);
        persistence.persist(sensorData);
    }

}
