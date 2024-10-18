package org.airController.persistence;

public class CsvToDb {

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final SensorDataCsv sensorDataCsv = new SensorDataCsv(csvFilePath);
        final SensorDataDb sensorDataDb = new SensorDataDb(tableName);
        PersistenceConverter.convert(sensorDataCsv, sensorDataDb);
    }

    public static void main(String[] args) {
        final CsvToDb csvToDb = new CsvToDb();
        csvToDb.persistToDbFromCsv(SensorDataPersistence.INDOOR_SENSOR_CSV_PATH, SensorDataPersistence.INDOOR_TABLE_NAME);
        csvToDb.persistToDbFromCsv(SensorDataPersistence.OUTDOOR_SENSOR_CSV_PATH, SensorDataPersistence.OUTDOOR_TABLE_NAME);
    }
}
