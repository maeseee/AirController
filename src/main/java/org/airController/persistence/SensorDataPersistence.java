package org.airController.persistence;

import org.airController.sensorValues.SensorData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorDataPersistence {

    String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorValuesV2.csv";
    String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorValuesV2.csv";
    String INDOOR_TABLE_NAME = "indoorSensor";
    String OUTDOOR_TABLE_NAME = "outdoorSensor";

    void persist(SensorData sensorData);

    List<SensorData> read();

    Optional<SensorData> getMostCurrentSensorData(LocalDateTime lastValidTimestamp);
}
