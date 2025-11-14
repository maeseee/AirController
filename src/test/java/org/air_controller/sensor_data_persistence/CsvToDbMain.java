package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.MariaDatabase;

public class CsvToDbMain {
    public static void main(String[] args) {
        final CsvToDb csvToDb = new CsvToDb(new MariaDatabase());
        csvToDb.persistToDbFromCsv(ClimateDataPointPersistence.INDOOR_SENSOR_CSV_PATH, ClimateDataPointPersistence.INDOOR_TABLE_NAME);
        csvToDb.persistToDbFromCsv(ClimateDataPointPersistence.OUTDOOR_SENSOR_CSV_PATH, ClimateDataPointPersistence.OUTDOOR_TABLE_NAME);
    }
}
