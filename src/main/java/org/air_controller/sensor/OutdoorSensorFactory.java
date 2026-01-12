package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor.open_weather_api_adapter.OpenWeatherApiAdapter;
import org.air_controller.sensor_data_persistence.*;

import java.util.List;

import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.OUTDOOR_SENSOR_CSV_PATH;
import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.OUTDOOR_TABLE_NAME;

@RequiredArgsConstructor
public class OutdoorSensorFactory extends SensorFactory {

    @Override
    protected ClimateSensor createSensor() {
        final ClimateDataPointPersistence persistence = new ClimateDataPoints(List.of(
                new ClimateDataPointsDbAccessor(new MariaDatabase(), OUTDOOR_TABLE_NAME),
                new ClimateDataPointsCsv(OUTDOOR_SENSOR_CSV_PATH)));
        final OpenWeatherApiSensor sensor = new OpenWeatherApiSensor();
        return new OpenWeatherApiAdapter(persistence, sensor);
    }
}
