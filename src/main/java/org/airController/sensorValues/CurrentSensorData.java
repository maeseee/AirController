package org.airController.sensorValues;

import org.airController.persistence.SensorDataPersistence;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class CurrentSensorData implements SensorData {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    private final LocalDateTime initializationTimestamp = LocalDateTime.now();
    private final SensorDataPersistence persistence;

    public CurrentSensorData(SensorDataPersistence persistence) {
        this.persistence = persistence;
    }

    @Override
    public Optional<Temperature> getTemperature() {
        final Optional<SensorData> sensorData = persistence.getMostCurrentSensorData(getLastValidTimestamp());
        return sensorData.flatMap(SensorData::getTemperature);
    }

    @Override
    public Optional<Humidity> getHumidity() {
        final Optional<SensorData> sensorData = persistence.getMostCurrentSensorData(getLastValidTimestamp());
        return sensorData.flatMap(SensorData::getHumidity);
    }

    @Override
    public Optional<CarbonDioxide> getCo2() {
        final Optional<SensorData> sensorData = persistence.getMostCurrentSensorData(getLastValidTimestamp());
        return sensorData.flatMap(SensorData::getCo2);
    }

    @Override
    public LocalDateTime getTimeStamp() {
        final Optional<SensorData> sensorData = persistence.getMostCurrentSensorData(getLastValidTimestamp());
        return sensorData.map(SensorData::getTimeStamp).orElse(initializationTimestamp);
    }

    private LocalDateTime getLastValidTimestamp() {
        return LocalDateTime.now().minus(SENSOR_INVALIDATION);
    }
}


