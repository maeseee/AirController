package org.air_controller.sensor.open_weather_api_adapter;

import lombok.NoArgsConstructor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Optional;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class JsonParser {

    public static Optional<ClimateDataPoint> parse(String jsonString) {
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final ClimateDataPoint dataPoint = new ClimateDataPointBuilder()
                    .setTemperatureKelvin(main.getDouble("temp"))
                    .setHumidityRelative(main.getDouble("humidity"))
                    .build();
            return Optional.of(dataPoint);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
