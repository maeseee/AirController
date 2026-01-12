package org.air_controller.sensor.open_weather_api_adapter;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.util.Optional;

@Slf4j
public class OpenWeatherApiAdapter extends ClimateSensor {
    public OpenWeatherApiAdapter(ClimateDataPointPersistence persistence, OpenWeatherApiSensor sensor) {
        super(persistence, sensor);
    }

    @Override
    protected Optional<ClimateDataPoint> parseResponse(String response) {
        if (response.isEmpty()) {
            log.error("Outdoor sensor request failed");
            return Optional.empty();
        }
        return JsonParser.parse(response);
    }

    @Override
    protected String sensorType() {
        return "OpenWeatherApi";
    }
}
