package org.air_controller.sensor;

import org.air_controller.persistence.Persistence;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.SensorDataCollection;
import org.air_controller.sensor_data_persistence.SensorDataCsv;
import org.air_controller.sensor_data_persistence.SensorDataDb;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;

import java.net.URISyntaxException;
import java.util.List;

public class SensorsBuilder {

    public Sensors build() {
        return new Sensors(createOutdoorSensor(), createIndoorSensor());
    }

    private static Sensor createOutdoorSensor() {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(new Persistence(), SensorDataPersistence.OUTDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.OUTDOOR_SENSOR_CSV_PATH)));
        return new OpenWeatherApiSensor(persistence);
    }

    private static Sensor createIndoorSensor() {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(new Persistence(), SensorDataPersistence.INDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.INDOOR_SENSOR_CSV_PATH)));
        try {
            return new QingPingSensor(persistence);
        } catch (URISyntaxException e) {
            throw new SensorException("Indoor sensor could not be created", e.getCause());
        }
    }
}
