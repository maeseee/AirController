package org.airController.persistence;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;

import java.time.LocalDateTime;

public class SensorValuePersistenceObserver implements IndoorSensorObserver, OutdoorSensorObserver {

    private static final String INDOOR_SENSOR_VALUES_FILE_PATH = "log/indoorSensorValues.csv";
    private static final String OUTDOOR_SENSOR_VALUES_FILE_PATH = "log/outdoorSensorValues.csv";

    private final SensorValuePersistence indoorValuePersistence = new SensorValueCsvWriter(INDOOR_SENSOR_VALUES_FILE_PATH);
    private final SensorValuePersistence outdoorValuePersistence = new SensorValueCsvWriter(OUTDOOR_SENSOR_VALUES_FILE_PATH);

    @Override
    public void updateIndoorAirValue(AirValue indoorAirValue) {
        indoorValuePersistence.persist(LocalDateTime.now(), indoorAirValue);
    }

    @Override
    public void updateOutdoorAirValue(AirValue outdoorAirValue) {
        outdoorValuePersistence.persist(LocalDateTime.now(), outdoorAirValue);
    }
}
