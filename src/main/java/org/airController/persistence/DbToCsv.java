package org.airController.persistence;

public class DbToCsv {
    static final String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorDataFromDb.csv";
    static final String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorDataFromDb.csv";

    public void persistToCsvFromDb(String tableName, String csvFilePath) {
        final SensorDataDb sensorDataDb = new SensorDataDb(tableName);
        final SensorDataCsv sensorDataCsv = new SensorDataCsv(csvFilePath);
        PersistenceConverter.convert(sensorDataDb, sensorDataCsv);
    }

    public static void main(String[] args) {
        final DbToCsv dbToCsv = new DbToCsv();
        dbToCsv.persistToCsvFromDb(SensorDataPersistence.INDOOR_TABLE_NAME, INDOOR_SENSOR_CSV_PATH);
        dbToCsv.persistToCsvFromDb(SensorDataPersistence.OUTDOOR_TABLE_NAME, OUTDOOR_SENSOR_CSV_PATH);
    }
}
