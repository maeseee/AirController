package org.air_controller.sensor.open_weather_api_adapter;

import lombok.NoArgsConstructor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class JsonParser {

    public static Optional<ClimateDataPoint> parseDataPoint(String jsonString) {
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject main = jsonObject.getJSONObject("main");
            final ClimateDataPoint dataPoint = new ClimateDataPointBuilder().setTemperatureKelvin(main.getDouble("temp")).setHumidityRelative(main.getDouble("humidity")).build();
            return Optional.of(dataPoint);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<SolarEvent> parsesSolarEvent(String jsonString) {
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final JSONObject sys = jsonObject.getJSONObject("sys");
            final ZonedDateTime sunrise = epochSecondsToLocalDate(sys.getLong("sunrise"));
            final ZonedDateTime sunset = epochSecondsToLocalDate(sys.getLong("sunset"));
            final SolarEvent solarEvent = new SolarEvent(sunrise, sunset);
            return Optional.of(solarEvent);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    private static ZonedDateTime epochSecondsToLocalDate(long epochSeconds) {
        return ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(epochSeconds),
                ZoneId.of("UTC"));
    }
}
