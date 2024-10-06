package org.airController.persistence;

import org.airController.sensorValues.SensorData;

import java.util.List;

public class DbToCsv {
    static final String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorDataFromDb.csv";
    static final String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorDataFromDb.csv";

    public void persistToCsvFromDb(String tableName, String csvFilePath) {
        final SensorDataDb indoorSensorDataDb = new SensorDataDb(tableName);
        final SensorDataCsv indoorSensorDataCsvWriter = new SensorDataCsv(csvFilePath);
        final List<SensorData> indoorSensorData = indoorSensorDataDb.read();
        indoorSensorData.forEach(indoorSensorDataCsvWriter::persist);
    }

    public static void main(String[] args) {
        final DbToCsv dbToCsv = new DbToCsv();
        dbToCsv.persistToCsvFromDb(SensorDataPersistenceObserver.INDOOR_TABLE_NAME, INDOOR_SENSOR_CSV_PATH);
        dbToCsv.persistToCsvFromDb(SensorDataPersistenceObserver.OUTDOOR_TABLE_NAME, OUTDOOR_SENSOR_CSV_PATH);
    }
}
