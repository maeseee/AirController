package org.air_controller.sensor_data_persistence;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.util.List;

@RequiredArgsConstructor
public class DbToCsv {

    private final DatabaseConnection database;

    public void persistToCsvFromDb(String tableName, String csvFilePath) {
        final SensorDataPersistence sensorDataDb = new SensorDataDb(database, tableName);
        final SensorDataPersistence sensorDataCsv = new SensorDataCsv(csvFilePath);
        final List<ClimateDataPoint> dataPoints = sensorDataDb.read();
        dataPoints.forEach(sensorDataCsv::persist);
    }
}
