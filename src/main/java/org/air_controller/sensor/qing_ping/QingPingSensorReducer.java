package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.*;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.OptionalDouble;

class QingPingSensorReducer {
    private final static Duration SENSOR_INVALIDATION_TIME = Duration.ofHours(1);

    public SensorData reduce(List<QingPingSensorData> sensorDataList) throws CalculationException, InvalidArgumentException {
        final List<QingPingSensorData> currentSensorDataList = sensorDataList.stream()
                .filter(sensorData -> sensorData.getTimeStamp().isAfter(ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION_TIME)))
                .toList();
        if (currentSensorDataList.isEmpty()) {
            throw new CalculationException("No current indoor data at the moment");
        }
        final Temperature temperature = getAverageTemperature(currentSensorDataList);
        final Humidity humidity = getAverageHumidity(currentSensorDataList);
        final CarbonDioxide co2 = getAverageCo2(currentSensorDataList);
        final ZonedDateTime time = getNewestTimestamp(currentSensorDataList);
        return new QingPingSensorData(temperature, humidity, co2, time);
    }

    private Temperature getAverageTemperature(List<QingPingSensorData> currentSensorDataList) throws InvalidArgumentException {
        final OptionalDouble averageTemperature = currentSensorDataList.stream()
                .filter(sensorData -> sensorData.getTemperature().isPresent())
                .mapToDouble(value -> value.getTemperature().get().getCelsius())
                .average();
        return averageTemperature.isPresent() ? Temperature.createFromCelsius(averageTemperature.getAsDouble()) : null;
    }

    private Humidity getAverageHumidity(List<QingPingSensorData> currentSensorDataList) throws InvalidArgumentException {
        final OptionalDouble averageHumidity = currentSensorDataList.stream()
                .filter(sensorData -> sensorData.getHumidity().isPresent())
                .mapToDouble(sensorData -> sensorData.getHumidity().get().getAbsoluteHumidity())
                .average();
        return averageHumidity.isPresent() ? Humidity.createFromAbsolute(averageHumidity.getAsDouble()) : null;
    }

    private CarbonDioxide getAverageCo2(List<QingPingSensorData> currentSensorDataList) throws InvalidArgumentException {
        final OptionalDouble averageCo2 = currentSensorDataList.stream()
                .filter(sensorData -> sensorData.getCo2().isPresent())
                .mapToDouble(value -> value.getCo2().get().getPpm())
                .average();
        return averageCo2.isPresent() ? CarbonDioxide.createFromPpm(averageCo2.getAsDouble()) : null;
    }

    private ZonedDateTime getNewestTimestamp(List<QingPingSensorData> currentSensorDataList) {
        return currentSensorDataList.stream()
                .map(QingPingSensorData::getTimeStamp)
                .max(ZonedDateTime::compareTo).orElse(ZonedDateTime.now(ZoneOffset.UTC));
    }
}