package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.*;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.OptionalDouble;

class SensorReducer {
    private static final Duration SENSOR_INVALIDATION_TIME = Duration.ofHours(1);

    public SensorData reduce(List<SensorData> sensorDataList) throws CalculationException, InvalidArgumentException {
        final List<SensorData> currentSensorDataList = sensorDataList.stream()
                .filter(sensorData -> sensorData.timestamp().isAfter(ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION_TIME)))
                .toList();
        if (currentSensorDataList.isEmpty()) {
            throw new CalculationException("No current indoor data at the moment");
        }
        return new SensorDataBuilder()
                .setTemperature(getAverageTemperature(currentSensorDataList))
                .setHumidity(getAverageHumidity(currentSensorDataList))
                .setCo2(getAverageCo2(currentSensorDataList))
                .setTime(getNewestTimestamp(currentSensorDataList))
                .build();
    }

    private Temperature getAverageTemperature(List<SensorData> currentSensorDataList) throws InvalidArgumentException {
        final OptionalDouble averageTemperature = currentSensorDataList.stream()
                .mapToDouble(value -> value.temperature().celsius())
                .average();
        return averageTemperature.isPresent() ? Temperature.createFromCelsius(averageTemperature.getAsDouble()) : null;
    }

    private Humidity getAverageHumidity(List<SensorData> currentSensorDataList) throws InvalidArgumentException {
        final OptionalDouble averageHumidity = currentSensorDataList.stream()
                .mapToDouble(sensorData -> sensorData.humidity().absoluteHumidity())
                .average();
        return averageHumidity.isPresent() ? Humidity.createFromAbsolute(averageHumidity.getAsDouble()) : null;
    }

    private CarbonDioxide getAverageCo2(List<SensorData> currentSensorDataList) throws InvalidArgumentException {
        final OptionalDouble averageCo2 = currentSensorDataList.stream()
                .filter(sensorData -> sensorData.co2().isPresent())
                .mapToDouble(value -> value.co2().get().ppm())
                .average();
        return averageCo2.isPresent() ? CarbonDioxide.createFromPpm(averageCo2.getAsDouble()) : null;
    }

    private ZonedDateTime getNewestTimestamp(List<SensorData> currentSensorDataList) {
        return currentSensorDataList.stream()
                .map(SensorData::timestamp)
                .max(ZonedDateTime::compareTo).orElse(ZonedDateTime.now(ZoneOffset.UTC));
    }
}
