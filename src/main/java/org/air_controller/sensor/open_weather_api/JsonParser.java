package org.air_controller.sensor.open_weather_api;

import lombok.NoArgsConstructor;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.Temperature;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Optional;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class JsonParser {

    public static Optional<WebSensorData> parse(String jsonString) {
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final double temperatureKelvin = main.getDouble("temp");
            final Temperature temperature = Temperature.createFromKelvin(temperatureKelvin);
            final double humidityRelative = main.getDouble("humidity");
            final Humidity humidity = Humidity.createFromRelative(humidityRelative, temperature);
            final WebSensorData sensorData = new WebSensorData(temperature, humidity);
            return Optional.of(sensorData);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
