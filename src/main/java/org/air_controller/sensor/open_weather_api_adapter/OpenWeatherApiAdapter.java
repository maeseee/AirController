package org.air_controller.sensor.open_weather_api_adapter;

import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class OpenWeatherApiAdapter extends ClimateSensor {

    private static final Logger logger = LogManager.getLogger(OpenWeatherApiAdapter.class);

    public OpenWeatherApiAdapter(ClimateDataPointPersistence persistence, OpenWeatherApiSensor sensor) {
        super(persistence, sensor);
    }

    protected Optional<ClimateDataPoint> parseResponse(String response) {
        if (response.isEmpty()) {
            logger.error("Outdoor sensor request failed");
            return Optional.empty();
        }
        return JsonParser.parse(response);
    }
}
