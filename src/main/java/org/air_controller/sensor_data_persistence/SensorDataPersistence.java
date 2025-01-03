package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.SensorData;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorDataPersistence {

    String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorValuesV2.csv";
    String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorValuesV2.csv";
    String INDOOR_TABLE_NAME = "indoorSensor";
    String OUTDOOR_TABLE_NAME = "outdoorSensor";

    void persist(SensorData sensorData);

    List<SensorData> read();

    Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp);
}
