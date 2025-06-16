package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.Persistence;
import org.air_controller.sensor_values.SensorData;

import java.util.List;

public class DbToCsv {

    public void persistToCsvFromDb(String tableName, String csvFilePath) {
        final SensorDataPersistence sensorDataDb = new SensorDataDb(new Persistence(), tableName);
        final SensorDataPersistence sensorDataCsv = new SensorDataCsv(csvFilePath);
        final List<SensorData> sensorData = sensorDataDb.read();
        sensorData.forEach(sensorDataCsv::persist);
    }
}
