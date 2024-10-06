package org.airController.persistence;

public class CsvToDb {
    static final String INDOOR_TABLE_NAME = "indoorSensorFromCsv";
    static final String OUTDOOR_TABLE_NAME = "outdoorSensorFromCsv";

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final SensorDataCsv sensorDataCsv = new SensorDataCsv(csvFilePath);
        final SensorDataDb sensorDataDb = new SensorDataDb(tableName);
        PersistenceConverter.convert(sensorDataCsv, sensorDataDb);
    }

    public static void main(String[] args) {
        final CsvToDb csvToDb = new CsvToDb();
        csvToDb.persistToDbFromCsv(SensorDataPersistenceObserver.INDOOR_SENSOR_CSV_PATH, INDOOR_TABLE_NAME);
        csvToDb.persistToDbFromCsv(SensorDataPersistenceObserver.OUTDOOR_SENSOR_CSV_PATH, OUTDOOR_TABLE_NAME);
    }
}
