package org.air_controller.sensor_data_persistence;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.sensor_values.SensorData;

import java.util.List;

@RequiredArgsConstructor
public class CsvToDb {

    private final DatabaseConnection database;

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final SensorDataPersistence sensorDataCsv = new SensorDataCsv(csvFilePath);
        final SensorDataPersistence sensorDataDb = new SensorDataDb(database, tableName);
        final List<SensorData> sensorData = sensorDataCsv.read();
        sensorData.forEach(sensorDataDb::persist);
    }
}
