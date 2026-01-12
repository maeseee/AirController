package org.air_controller.sensor.ping_ping_adapter;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

@Slf4j
class ListDevicesJsonParser {
    public Optional<ClimateDataPoint> parseDeviceListResponse(String jsonString, String macAddress) {
        // https://developer.qingping.co/main/openApi
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONArray devices = jsonObject.getJSONArray("devices");
            final Optional<JSONObject> deviceData = getDevicesData(devices, macAddress);
            if (deviceData.isEmpty()) {
                log.info("No device with MAC-Address {} found!", macAddress);
                return Optional.empty();
            }
            final ClimateDataPoint dataPoint = getDataPoint(deviceData.get());
            return Optional.of(dataPoint);
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

    private ClimateDataPoint getDataPoint(JSONObject deviceData) throws InvalidArgumentException {
        final double temperatureCelsius = getDoubleValue("temperature", deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Invalid temperature"));
        final double humidityRelative = getDoubleValue("humidity", deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Invalid humidity"));
        final OptionalDouble co2Optional = getDoubleValue("co2", deviceData);
        final Double co2Ppm = co2Optional.isPresent() ? co2Optional.getAsDouble() : null;
        validateTimeStamp(deviceData);
        return new ClimateDataPointBuilder()
                .setTemperatureCelsius(temperatureCelsius)
                .setHumidityRelative(humidityRelative)
                .setCo2(co2Ppm)
                .build();
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

    private void validateTimeStamp(JSONObject deviceData) throws InvalidArgumentException {
        // QingPing sometimes has a strange time. Therefor I just only if it is about the current time
        final Duration errorTolerance = Duration.ofHours(3);
        final long timeFromEpoch = getTimestamp(deviceData)
                .orElseThrow(() -> new InvalidArgumentException("Invalid timestamp"));
        final ZonedDateTime time = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(timeFromEpoch),
                ZoneOffset.UTC);
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (now.plus(errorTolerance).isBefore(time) || now.minus(errorTolerance).isAfter(time)) {
            throw new InvalidArgumentException("Invalid timestamp");
        }
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
