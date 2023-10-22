package org.util;

import org.entities.AirValues;
import org.entities.Humidity;
import org.entities.Temperature;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonParser {

    public static AirValues parse(String jsonString) {
        Temperature temperature;
        Humidity humidity;
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final double temperatureKelvin = main.getDouble("temp");
            temperature = Temperature.createTemperatureFromKelvin(temperatureKelvin);
            final double humidityRelative = main.getDouble("humidity");
            humidity = new Humidity(humidityRelative);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new AirValues(temperature, humidity);
    }
}
