package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor.open_weather_api_adapter.OpenWeatherApiAdapter;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;

@RequiredArgsConstructor
public class OutdoorSensorFactory extends SensorFactory {

    @Override
    protected ClimateSensor createSensor(ClimateDataPointPersistence persistence) {
        final OpenWeatherApiSensor sensor = new OpenWeatherApiSensor();
        return new OpenWeatherApiAdapter(persistence, sensor);
    }
}
