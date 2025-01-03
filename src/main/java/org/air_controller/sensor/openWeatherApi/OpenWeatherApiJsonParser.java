package org.air_controller.sensor.openWeatherApi;

import org.air_controller.sensorValues.Humidity;
import org.air_controller.sensorValues.Temperature;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Optional;

class OpenWeatherApiJsonParser {

    public static Optional<OpenWeatherApiSensorData> parse(String jsonString) {
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final double temperatureKelvin = main.getDouble("temp");
            final Temperature temperature = Temperature.createFromKelvin(temperatureKelvin);
            final double humidityRelative = main.getDouble("humidity");
            final Humidity humidity = Humidity.createFromRelative(humidityRelative, temperature);
            final OpenWeatherApiSensorData sensorData = new OpenWeatherApiSensorData(temperature, humidity);
            return Optional.of(sensorData);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
