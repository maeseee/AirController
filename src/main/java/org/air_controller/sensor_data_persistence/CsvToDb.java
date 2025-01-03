package org.air_controller.sensor_data_persistence;

import org.air_controller.sensorValues.SensorData;

import java.util.List;

public class CsvToDb {

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final SensorDataPersistence sensorDataCsv = new SensorDataCsv(csvFilePath);
        final SensorDataPersistence sensorDataDb = new SensorDataDb(tableName);
        final List<SensorData> sensorData = sensorDataCsv.read();
        sensorData.forEach(sensorDataDb::persist);
    }

    public static void main(String[] args) {
        final CsvToDb csvToDb = new CsvToDb();
        csvToDb.persistToDbFromCsv(SensorDataPersistence.INDOOR_SENSOR_CSV_PATH, SensorDataPersistence.INDOOR_TABLE_NAME);
        csvToDb.persistToDbFromCsv(SensorDataPersistence.OUTDOOR_SENSOR_CSV_PATH, SensorDataPersistence.OUTDOOR_TABLE_NAME);
    }
}
