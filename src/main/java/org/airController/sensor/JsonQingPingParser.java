package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

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
            final Optional<JSONObject> deviceData = getDevicesData(devices, macAddress);
            if (deviceData.isEmpty()) {
                logger.info("No device MAC-Address {} found!", macAddress);
                return Optional.empty();
            }
            final AirValue airValue = getAirValue(deviceData.get());
            return Optional.of(airValue);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getDevicesData(JSONArray devices, String macAddress) {
        for (int deviceNumber = 0; deviceNumber < devices.length(); deviceNumber++) {
            final JSONObject device = (JSONObject) devices.get(deviceNumber);
            final JSONObject info = device.getJSONObject("info");
            final String mac = info.getString("mac");
            if (Objects.equals(macAddress, mac)) {
                final JSONObject data = device.getJSONObject("data");
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    private AirValue getAirValue(JSONObject deviceData) throws IOException {
        final double temperatureCelsius = getDoubleValue("temperature", deviceData).orElseThrow();
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final double humidityRelative = getDoubleValue("humidity", deviceData).orElseThrow();
        final Humidity humidity = Humidity.createFromRelative(humidityRelative);
        final OptionalDouble co2Optinal = getDoubleValue("co2", deviceData);
        final CarbonDioxide co2 = co2Optinal.isPresent() ? CarbonDioxide.createFromPpm(co2Optinal.getAsDouble()) : null;
        final long timeFromEpoch = getLongValue("timestamp", deviceData).orElseThrow();
        final LocalDateTime time = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timeFromEpoch),
                ZoneId.systemDefault());
        return new AirValue(temperature, humidity, co2, time);
    }

    private OptionalDouble getDoubleValue(String attribute, JSONObject deviceData) {
        try {
            final JSONObject data = deviceData.getJSONObject(attribute);
            return OptionalDouble.of(data.getDouble("value"));
        } catch (JSONException exception) {
            // Intentionally left empty
        }
        return OptionalDouble.empty();
    }

    private OptionalLong getLongValue(String attribute, JSONObject deviceData) {
        try {
            final JSONObject data = deviceData.getJSONObject(attribute);
            return OptionalLong.of(data.getLong("value"));
        } catch (JSONException exception) {
            // Intentionally left empty
        }
        return OptionalLong.empty();
    }

}
