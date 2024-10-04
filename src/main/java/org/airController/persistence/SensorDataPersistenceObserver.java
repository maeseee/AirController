package org.airController.persistence;

import org.airController.sensor.IndoorSensorObserver;
import org.airController.sensor.OutdoorSensorObserver;
import org.airController.sensorValues.SensorData;

import java.util.List;

public class SensorDataPersistenceObserver implements IndoorSensorObserver, OutdoorSensorObserver {

    private static final String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorValuesV2.csv";
    private static final String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorValuesV2.csv";
    private static final String INDOOR_TABLE_NAME = "indoorSensor";
    private static final String OUTDOOR_TABLE_NAME = "outdoorSensor";

    private final List<SensorDataPersistence> indoorValuePersistence = List.of(
            new SensorDataCsvWriter(INDOOR_SENSOR_CSV_PATH),
            new SensorDataDb(INDOOR_TABLE_NAME));
    private final List<SensorDataPersistence> outdoorValuePersistence = List.of(
            new SensorDataCsvWriter(OUTDOOR_SENSOR_CSV_PATH),
            new SensorDataDb(OUTDOOR_TABLE_NAME));

    @Override
    public void updateIndoorSensorData(SensorData indoorSensorData) {
        indoorValuePersistence.forEach(persistence -> persistence.persist(indoorSensorData));
    }

    @Override
    public void updateOutdoorSensorData(SensorData outdoorSensorData) {
        outdoorValuePersistence.forEach(persistence -> persistence.persist(outdoorSensorData));
    }
}
