package org.air_controller.sensor.open_weather_api_adapter;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class OpenWeatherApiAdapter extends ClimateSensor {
    public OpenWeatherApiAdapter(@Qualifier("outdoorPersistence") ClimateDataPointPersistence persistence, OpenWeatherApiSensor sensor) {
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
}
