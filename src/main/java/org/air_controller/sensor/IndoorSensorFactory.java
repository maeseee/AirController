package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.SensorDataCollection;
import org.air_controller.sensor_data_persistence.SensorDataCsv;
import org.air_controller.sensor_data_persistence.SensorDataDb;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;

import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
public class IndoorSensorFactory extends SensorFactory {

    @Override
    protected Sensor createSensor() {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(new MariaDatabase(), SensorDataPersistence.INDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.INDOOR_SENSOR_CSV_PATH)));
        try {
            return new QingPingSensor(persistence);
        } catch (URISyntaxException e) {
            throw new SensorException("Indoor sensor could not be created", e.getCause());
        }
    }
}
