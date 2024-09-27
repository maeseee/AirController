package org.airController.sensor.qingPing;

import org.airController.sensor.IndoorSensor;
import org.airController.sensor.IndoorSensorObserver;
import org.airController.sensorValues.InvaildArgumentException;
import org.airController.sensorValues.SensorData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class QingPingSensor implements IndoorSensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final List<IndoorSensorObserver> observers = new ArrayList<>();
    private final QingPingAccessToken accessToken;
    private final QingPingListDevices listDevices;
    private final QingPingSensorReducer sensorReducer = new QingPingSensorReducer();
    private IndoorSensor backupSensor;

    public QingPingSensor() throws URISyntaxException {
        this(new QingPingAccessToken(), new QingPingListDevices());
    }

    QingPingSensor(QingPingAccessToken accessToken, QingPingListDevices listDevices) {
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

    private void doRun() throws CommunicationException, IOException, URISyntaxException, InvaildArgumentException, CalculationException {
        final String token = accessToken.readToken();
        final List<QingPingSensorData> sensorDataList = listDevices.readSensorDataList(token);
        final SensorData sensorData = sensorReducer.reduce(sensorDataList);
        notifyObservers(sensorData);
    }

    @Override
    public void addObserver(IndoorSensorObserver observer) {
        observers.add(observer);
    }

    public void addBackupSensor(IndoorSensor backupSensor) {
        this.backupSensor = backupSensor;
    }

    private void notifyObservers(SensorData sensorData) {
        logger.info("New indoor sensor data: {}", sensorData);
        observers.forEach(observer -> observer.updateIndoorSensorData(sensorData));
    }

}
