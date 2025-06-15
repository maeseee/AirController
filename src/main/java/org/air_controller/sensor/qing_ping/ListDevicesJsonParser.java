package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.Temperature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

class ListDevicesJsonParser {
    private static final Logger logger = LogManager.getLogger(ListDevicesJsonParser.class);

    public Optional<HwSensorData> parseDeviceListResponse(String jsonString, String macAddress) {
        // https://developer.qingping.co/main/openApi
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONArray devices = jsonObject.getJSONArray("devices");
            final Optional<JSONObject> deviceData = getDevicesData(devices, macAddress);
            if (deviceData.isEmpty()) {
                logger.info("No device with MAC-Address {} found!", macAddress);
                return Optional.empty();
            }
            final HwSensorData sensorData = getSensorData(deviceData.get());
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

    private HwSensorData getSensorData(JSONObject deviceData) throws InvalidArgumentException {
        final double temperatureCelsius = getDoubleValue("temperature", deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Invalid temperature"));
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final double humidityRelative = getDoubleValue("humidity", deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Invalid humidity"));
        final Humidity humidity = Humidity.createFromRelative(humidityRelative, temperature);
        final OptionalDouble co2Optional = getDoubleValue("co2", deviceData);
        final CarbonDioxide co2 = co2Optional.isPresent() ? CarbonDioxide.createFromPpm(co2Optional.getAsDouble()) : null;
        final long timeFromEpoch = getTimestamp(deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Invalid timestamp"));
        final ZonedDateTime time = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(timeFromEpoch),
                ZoneOffset.UTC);
        return new HwSensorData(temperature, humidity, co2, time);
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

    private OptionalLong getTimestamp(JSONObject deviceData) {
        try {
            final JSONObject data = deviceData.getJSONObject("timestamp");
            return OptionalLong.of(data.getLong("value"));
        } catch (JSONException exception) {
            // Intentionally left empty
        }
        return OptionalLong.empty();
    }
}
