package org.airController.persistence;

import org.airController.sensor.SensorObserver;
import org.airController.sensorValues.SensorData;

import java.util.List;

public class SensorDataPersistenceObserver implements SensorObserver {

    public static final String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorValuesV2.csv";
    public static final String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorValuesV2.csv";
    public static final String INDOOR_TABLE_NAME = "indoorSensor";
    public static final String OUTDOOR_TABLE_NAME = "outdoorSensor";

    private final List<SensorDataPersistence> persistences;

    public SensorDataPersistenceObserver(List<SensorDataPersistence> persistences) {
        this.persistences = persistences;
    }

    @Override
    public void updateSensorData(SensorData sensorData) {
        persistences.forEach(persistence -> persistence.persist(sensorData));
    }
}
