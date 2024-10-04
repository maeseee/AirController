package org.airController.persistence;

import org.airController.sensor.IndoorSensorObserver;
import org.airController.sensor.OutdoorSensorObserver;
import org.airController.sensorValues.SensorData;

import java.util.List;

public class SensorDataPersistenceObserver implements IndoorSensorObserver, OutdoorSensorObserver {

    private static final String INDOOR_SENSOR_VALUES_FILE_PATH = "log/indoorSensorValuesV2.csv";
    private static final String OUTDOOR_SENSOR_VALUES_FILE_PATH = "log/outdoorSensorValuesV2.csv";

    private final List<SensorDataPersistence> indoorValuePersistence = List.of(new SensorDataCsvWriter(INDOOR_SENSOR_VALUES_FILE_PATH));
    private final List<SensorDataPersistence> outdoorValuePersistence = List.of(new SensorDataCsvWriter(OUTDOOR_SENSOR_VALUES_FILE_PATH));

    @Override
    public void updateIndoorSensorData(SensorData indoorSensorData) {
        indoorValuePersistence.forEach(persistence -> persistence.persist(indoorSensorData));
    }

    @Override
    public void updateOutdoorSensorData(SensorData outdoorSensorData) {
        outdoorValuePersistence.forEach(persistence -> persistence.persist(outdoorSensorData));
    }
}
