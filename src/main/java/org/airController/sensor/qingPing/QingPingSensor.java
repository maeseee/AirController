package org.airController.sensor.qingPing;

import org.airController.sensor.Sensor;
import org.airController.sensor.SensorObserver;
import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.SensorData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class QingPingSensor implements Sensor {

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final List<SensorObserver> observers = new ArrayList<>();
    private final QingPingAccessToken accessToken;
    private final QingPingListDevices listDevices;
    private final QingPingSensorReducer sensorReducer = new QingPingSensorReducer();
    private Sensor backupSensor;

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

    private void doRun() throws CommunicationException, IOException, URISyntaxException, InvalidArgumentException, CalculationException {
        final String token = accessToken.readToken();
        final List<QingPingSensorData> sensorDataList = listDevices.readSensorDataList(token);
        final SensorData sensorData = sensorReducer.reduce(sensorDataList);
        notifyObservers(sensorData);
    }

    @Override
    public void addObserver(SensorObserver observer) {
        observers.add(observer);
    }

    public void addBackupSensor(Sensor backupSensor) {
        this.backupSensor = backupSensor;
    }

    private void notifyObservers(SensorData sensorData) {
        logger.info("New indoor sensor data: {}", sensorData);
        observers.forEach(observer -> observer.updateSensorData(sensorData));
    }

}
