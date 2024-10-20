package org.airController.sensorDataPersistence;

import org.airController.sensorValues.SensorData;

import java.util.List;

public class DbToCsv {
    static final String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorDataFromDb.csv";
    static final String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorDataFromDb.csv";

    public void persistToCsvFromDb(String tableName, String csvFilePath) {
        final SensorDataPersistence sensorDataDb = new SensorDataDb(tableName);
        final SensorDataPersistence sensorDataCsv = new SensorDataCsv(csvFilePath);
        final List<SensorData> sensorData = sensorDataDb.read();
        sensorData.forEach(sensorDataCsv::persist);
    }

    public static void main(String[] args) {
        final DbToCsv dbToCsv = new DbToCsv();
        dbToCsv.persistToCsvFromDb(SensorDataPersistence.INDOOR_TABLE_NAME, INDOOR_SENSOR_CSV_PATH);
        dbToCsv.persistToCsvFromDb(SensorDataPersistence.OUTDOOR_TABLE_NAME, OUTDOOR_SENSOR_CSV_PATH);
    }
}
