package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.SensorData;

import java.util.List;

public class CsvToDb {

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final SensorDataPersistence sensorDataCsv = new SensorDataCsv(csvFilePath);
        final SensorDataPersistence sensorDataDb = new SensorDataDb(tableName);
        final List<SensorData> sensorData = sensorDataCsv.read();
        sensorData.forEach(sensorDataDb::persist);
    }
}
