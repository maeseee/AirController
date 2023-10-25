package org.util;

import org.entities.AirVO;
import org.entities.Humidity;
import org.entities.Temperature;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonParser {

    public static AirVO parse(String jsonString) {
        Temperature temperature;
        Humidity humidity;
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final double temperatureKelvin = main.getDouble("temp");
            temperature = Temperature.createFromKelvin(temperatureKelvin);
            final double humidityRelative = main.getDouble("humidity");
            humidity = Humidity.createFromRelative(humidityRelative);
        } catch (Exception e) {
            return null;
        }
        return new AirVO(temperature, humidity);
    }
}
