package org.airController.util;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Optional;

public class JsonParser {

    public static Optional<AirValue> parse(String jsonString) {
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final double temperatureKelvin = main.getDouble("temp");
            final Temperature temperature = Temperature.createFromKelvin(temperatureKelvin);
            final double humidityRelative = main.getDouble("humidity");
            final Humidity humidity = Humidity.createFromRelative(humidityRelative);
            final AirValue airValue = new AirValue(temperature, humidity);
            return Optional.of(airValue);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
