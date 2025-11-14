package org.air_controller.sensor.ping_ping_adapter;

import org.air_controller.sensor_values.*;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.OptionalDouble;

class SensorReducer {
    private static final Duration SENSOR_INVALIDATION_TIME = Duration.ofHours(1);

    public ClimateDataPoint reduce(List<ClimateDataPoint> dataPoints) throws CalculationException, InvalidArgumentException {
        final List<ClimateDataPoint> currentDataPoints = dataPoints.stream()
                .filter(dataPoint -> dataPoint.timestamp().isAfter(ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION_TIME)))
                .toList();
        if (currentDataPoints.isEmpty()) {
            throw new CalculationException("No current indoor data at the moment");
        }
        return new ClimateDataPointBuilder()
                .setTemperature(getAverageTemperature(currentDataPoints))
                .setHumidity(getAverageHumidity(currentDataPoints))
                .setCo2(getAverageCo2(currentDataPoints))
                .setTime(getNewestTimestamp(currentDataPoints))
                .build();
    }

    private Temperature getAverageTemperature(List<ClimateDataPoint> currentDataPoints) throws InvalidArgumentException {
        final OptionalDouble averageTemperature = currentDataPoints.stream()
                .mapToDouble(value -> value.temperature().celsius())
                .average();
        return averageTemperature.isPresent() ? Temperature.createFromCelsius(averageTemperature.getAsDouble()) : null;
    }

    private Humidity getAverageHumidity(List<ClimateDataPoint> currentDataPoints) throws InvalidArgumentException {
        final OptionalDouble averageHumidity = currentDataPoints.stream()
                .mapToDouble(dataPoint -> dataPoint.humidity().absoluteHumidity())
                .average();
        return averageHumidity.isPresent() ? Humidity.createFromAbsolute(averageHumidity.getAsDouble()) : null;
    }

    private CarbonDioxide getAverageCo2(List<ClimateDataPoint> currentDataPoints) throws InvalidArgumentException {
        final OptionalDouble averageCo2 = currentDataPoints.stream()
                .filter(dataPoint -> dataPoint.co2().isPresent())
                .mapToDouble(value -> value.co2().get().ppm())
                .average();
        return averageCo2.isPresent() ? CarbonDioxide.createFromPpm(averageCo2.getAsDouble()) : null;
    }

    private ZonedDateTime getNewestTimestamp(List<ClimateDataPoint> currentDataPoints) {
        return currentDataPoints.stream()
                .map(ClimateDataPoint::timestamp)
                .max(ZonedDateTime::compareTo).orElse(ZonedDateTime.now(ZoneOffset.UTC));
    }
}
