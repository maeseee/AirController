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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

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

    public Optional<AirValue> parseDeviceListResponse(String jsonString, List<String> macAddresses) {
        // https://developer.qingping.co/main/openApi
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONArray devices = jsonObject.getJSONArray("devices");
            final List<JSONObject> devicesData = getDevicesData(devices, macAddresses);
            if (devicesData.isEmpty()) {
                logger.info("No device with MAC-Addresses " + macAddresses + " found!");
                return Optional.empty();
            }
            final AirValue airValue = getAirValue(devicesData);
            return Optional.of(airValue);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<JSONObject> getDevicesData(JSONArray devices, List<String> macAddresses) {
        final List<JSONObject> devicesData = new ArrayList<>();
        for (int deviceNumber = 0; deviceNumber < devices.length(); deviceNumber++) {
            final JSONObject device = (JSONObject) devices.get(0);
            final JSONObject info = device.getJSONObject("info");
            final String mac = info.getString("mac");
            if (macAddresses.contains(mac)) {
                final JSONObject data = device.getJSONObject("data");
                devicesData.add(data);
            }
        }
        return devicesData;
    }

    private AirValue getAirValue(List<JSONObject> devicesData) throws IOException {
        final List<Double> temperatureList = new ArrayList<>();
        final List<Double> humidityList = new ArrayList<>();
        final List<Double> co2List = new ArrayList<>();
        for (JSONObject data : devicesData) {
            addValueToList("temperature", data, temperatureList);
            addValueToList("humidity", data, humidityList);
            addValueToList("co2", data, co2List);
        }

        return createAirValue(temperatureList, humidityList, co2List);
    }

    private void addValueToList(String attribute, JSONObject deviceData, List<Double> resultList) {
        try {
            final JSONObject data = deviceData.getJSONObject(attribute);
            resultList.add(data.getDouble("value"));
        } catch (JSONException exception) {
            // Intentionally left empty
        }
    }

    private AirValue createAirValue(List<Double> temperatureList, List<Double> humidityList, List<Double> co2List) throws IOException {
        final double temperatureAverage = temperatureList.stream().mapToDouble(d -> d).average().orElseThrow();
        final Temperature temperature = Temperature.createFromCelsius(temperatureAverage);
        final double humidityAverage = humidityList.stream().mapToDouble(d -> d).average().orElseThrow();
        final Humidity humidity = Humidity.createFromRelative(humidityAverage);
        final OptionalDouble co2Average = co2List.stream().mapToDouble(d -> d).average();
        final CarbonDioxide co2 = co2Average.isPresent() ? CarbonDioxide.createFromPpm(co2Average.getAsDouble()) : null;
        return new AirValue(temperature, humidity, co2);
    }
}
