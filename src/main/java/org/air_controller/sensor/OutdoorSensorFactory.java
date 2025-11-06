package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor_data_persistence.*;

import java.util.List;

@RequiredArgsConstructor
public class OutdoorSensorFactory extends SensorFactory {

    @Override
    protected Sensor createSensor() {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(new MariaDatabase(), SensorDataPersistence.OUTDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.OUTDOOR_SENSOR_CSV_PATH)));
        return new OpenWeatherApiSensor(persistence);
    }
}
