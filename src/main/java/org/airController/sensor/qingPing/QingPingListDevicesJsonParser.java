package org.airController.sensor.qingPing;

import org.airController.sensorValues.CarbonDioxide;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.Temperature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

class QingPingListDevicesJsonParser {
    private static final Logger logger = LogManager.getLogger(QingPingListDevicesJsonParser.class);

    public Optional<QingPingSensorData> parseDeviceListResponse(String jsonString, String macAddress) {
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
            final QingPingSensorData sensorData = getSensorData(deviceData.get());
            return Optional.of(sensorData);
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

    private QingPingSensorData getSensorData(JSONObject deviceData) throws InvalidArgumentException {
        final double temperatureCelsius = getDoubleValue("temperature", deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Not Possible"));
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final double humidityRelative = getDoubleValue("humidity", deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Not Possible"));
        final Humidity humidity = Humidity.createFromRelative(humidityRelative, temperature);
        final OptionalDouble co2Optional = getDoubleValue("co2", deviceData);
        final CarbonDioxide co2 = co2Optional.isPresent() ? CarbonDioxide.createFromPpm(co2Optional.getAsDouble()) : null;
        final long timeFromEpoch = getLongValue("timestamp", deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Not Possible"));
        final LocalDateTime time = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timeFromEpoch),
                ZoneId.systemDefault());
        return new QingPingSensorData(temperature, humidity, co2, time);
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
