package org.air_controller.sensor.dht22;

import org.air_controller.sensor_data_persistence.DbToCsv;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;

public class DbToCsvMain {
    static final String INDOOR_SENSOR_CSV_PATH = "log/indoorSensorDataFromDb.csv";
    static final String OUTDOOR_SENSOR_CSV_PATH = "log/outdoorSensorDataFromDb.csv";

    public static void main(String[] args) {
        final DbToCsv dbToCsv = new DbToCsv();
        dbToCsv.persistToCsvFromDb(SensorDataPersistence.INDOOR_TABLE_NAME, INDOOR_SENSOR_CSV_PATH);
        dbToCsv.persistToCsvFromDb(SensorDataPersistence.OUTDOOR_TABLE_NAME, OUTDOOR_SENSOR_CSV_PATH);
    }
}
