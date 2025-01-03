package org.air_controller.sensor.qingPing;

import com.google.inject.internal.Nullable;
import lombok.Getter;
import org.air_controller.sensorDataPersistence.SensorDataPersistence;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensorValues.InvalidArgumentException;
import org.air_controller.sensorValues.SensorData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


public class QingPingSensor implements Sensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    @Getter
    private final SensorDataPersistence persistence;
    @Nullable
    private final Sensor backupSensor;
    private final QingPingAccessToken accessToken;
    private final QingPingListDevices listDevices;
    private final QingPingSensorReducer sensorReducer = new QingPingSensorReducer();

    public QingPingSensor(SensorDataPersistence persistence, Sensor backupSensor) throws URISyntaxException {
        this(persistence, backupSensor, new QingPingAccessToken(), new QingPingListDevices());
    }

    QingPingSensor(SensorDataPersistence persistence, Sensor backupSensor, QingPingAccessToken accessToken, QingPingListDevices listDevices) {
        this.persistence = persistence;
        this.backupSensor = backupSensor;
        this.accessToken = accessToken;
        this.listDevices = listDevices;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            logger.error("Exception in QingPing sensor loop:", exception);
            if (backupSensor != null) {
                backupSensor.run();
            }
        }
    }

    private void doRun() throws CommunicationException, IOException, URISyntaxException, InvalidArgumentException, CalculationException {
        final String token = accessToken.readToken();
        final List<QingPingSensorData> sensorDataList = listDevices.readSensorDataList(token);
        final SensorData sensorData = sensorReducer.reduce(sensorDataList);
        notifyObservers(sensorData);
    }

    private void notifyObservers(SensorData sensorData) {
        logger.info("New indoor sensor data: {}", sensorData);
        persistence.persist(sensorData);
    }

}
