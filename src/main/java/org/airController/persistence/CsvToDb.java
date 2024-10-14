package org.airController.persistence;

import static org.airController.persistence.SensorDataPersistenceObserver.*;

public class CsvToDb {

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final SensorDataCsv sensorDataCsv = new SensorDataCsv(csvFilePath);
        final SensorDataDb sensorDataDb = new SensorDataDb(tableName);
        PersistenceConverter.convert(sensorDataCsv, sensorDataDb);
    }

    public static void main(String[] args) {
        final CsvToDb csvToDb = new CsvToDb();
        csvToDb.persistToDbFromCsv(INDOOR_SENSOR_CSV_PATH, INDOOR_TABLE_NAME);
        csvToDb.persistToDbFromCsv(OUTDOOR_SENSOR_CSV_PATH, OUTDOOR_TABLE_NAME);
    }
}
