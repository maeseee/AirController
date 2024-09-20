package org.airController.persistence;

import org.airController.controllers.SensorData;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;

public class SensorValuePersistenceObserver implements IndoorSensorObserver, OutdoorSensorObserver {

    private static final String INDOOR_SENSOR_VALUES_FILE_PATH = "log/indoorSensorValuesV2.csv";
    private static final String OUTDOOR_SENSOR_VALUES_FILE_PATH = "log/outdoorSensorValuesV2.csv";

    private final SensorValuePersistence indoorValuePersistence = new SensorValueCsvWriter(INDOOR_SENSOR_VALUES_FILE_PATH);
    private final SensorValuePersistence outdoorValuePersistence = new SensorValueCsvWriter(OUTDOOR_SENSOR_VALUES_FILE_PATH);

    @Override
    public void updateIndoorSensorValue(SensorData indoorSensorData) {
        indoorValuePersistence.persist(indoorSensorData);
    }

    @Override
    public void updateOutdoorSensorValue(SensorData outdoorSensorData) {
        outdoorValuePersistence.persist(outdoorSensorData);
    }
}
