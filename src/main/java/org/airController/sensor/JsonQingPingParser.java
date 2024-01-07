package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Optional;

class JsonQingPingParser {

    public static Optional<QingPingAccessToken> parseAccessTokenResponse(String jsonString) {
        // https://developer.qingping.co/main/oauthApi
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final String accessToken = jsonObject.getString("access_token");
            final int expiresIn = jsonObject.getInt("expires_in");
            return Optional.of(new QingPingAccessToken(accessToken,expiresIn));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<AirValue> parseDeviceListResponse(String jsonString) {
        // https://developer.qingping.co/main/openApi
        // Only one device supported so far!
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONArray devices = jsonObject.getJSONArray("devices");
            final JSONObject device = (JSONObject) devices.get(0);
            final JSONObject data = device.getJSONObject("data");
            final JSONObject temperatureData = data.getJSONObject("temperature");
            final double temperatureCelsius = temperatureData.getDouble("value");
            final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
            final JSONObject humidityData = data.getJSONObject("humidity");
            final double humidityRelative = humidityData.getDouble("value");
            final Humidity humidity = Humidity.createFromRelative(humidityRelative);
            final AirValue airValue = new AirValue(temperature, humidity);
            return Optional.of(airValue);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
