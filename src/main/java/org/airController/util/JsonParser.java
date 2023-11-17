package org.airController.util;

import org.airController.entities.AirVO;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonParser {

    public static AirVO parse(String jsonString) {
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final double temperatureKelvin = main.getDouble("temp");
            final Temperature temperature = Temperature.createFromKelvin(temperatureKelvin);
            final double humidityRelative = main.getDouble("humidity");
            final Humidity humidity = Humidity.createFromRelative(humidityRelative);
            return new AirVO(temperature, humidity);
        } catch (Exception e) {
            return null;
        }
    }
}
