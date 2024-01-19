package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

class JsonQingPingParser {
    private static final Logger logger = LogManager.getLogger(JsonQingPingParser.class);

    public Optional<QingPingAccessToken> parseAccessTokenResponse(String jsonString) {
        // https://developer.qingping.co/main/oauthApi
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final String accessToken = jsonObject.getString("access_token");
            final int expiresIn = jsonObject.getInt("expires_in");
            return Optional.of(new QingPingAccessToken(accessToken, expiresIn));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<AirValue> parseDeviceListResponse(String jsonString, String macAddress) {
        // https://developer.qingping.co/main/openApi
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONArray devices = jsonObject.getJSONArray("devices");
            final Optional<JSONObject> deviceData = getDevice(devices, macAddress);
            if (deviceData.isEmpty()) {
                logger.info("Device with MAC-Address " + macAddress + " is not available!");
                return Optional.empty();
            }
            final AirValue airValue = getAirValue(deviceData.get());
            return Optional.of(airValue);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getDevice(JSONArray devices, String macAddress) {
        for (int deviceNumber = 0; deviceNumber < devices.length(); deviceNumber++) {
            final JSONObject device = (JSONObject) devices.get(0);
            final JSONObject info = device.getJSONObject("info");
            final String mac = info.getString("mac");
            if (Objects.equals(mac, macAddress)) {
                final JSONObject data = device.getJSONObject("data");
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    private AirValue getAirValue(JSONObject deviceData) throws IOException {
        final JSONObject temperatureData = deviceData.getJSONObject("temperature");
        final double temperatureCelsius = temperatureData.getDouble("value");
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final JSONObject humidityData = deviceData.getJSONObject("humidity");
        final double humidityRelative = humidityData.getDouble("value");
        final Humidity humidity = Humidity.createFromRelative(humidityRelative);
        return new AirValue(temperature, humidity);
    }
}
