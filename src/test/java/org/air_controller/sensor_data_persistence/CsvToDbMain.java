package org.air_controller.sensor_data_persistence;

public class CsvToDbMain {
    public static void main(String[] args) {
        final CsvToDb csvToDb = new CsvToDb();
        csvToDb.persistToDbFromCsv(SensorDataPersistence.INDOOR_SENSOR_CSV_PATH, SensorDataPersistence.INDOOR_TABLE_NAME);
        csvToDb.persistToDbFromCsv(SensorDataPersistence.OUTDOOR_SENSOR_CSV_PATH, SensorDataPersistence.OUTDOOR_TABLE_NAME);
    }
}
