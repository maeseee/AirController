package org.air_controller.sensor_values;

import org.air_controller.sensor_data_persistence.SensorDataPersistence;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

public class CurrentSensorData implements SensorData {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    private final ZonedDateTime initializationTimestamp = ZonedDateTime.now(ZoneOffset.UTC);
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
    public ZonedDateTime getTimeStamp() {
        final Optional<SensorData> sensorData = persistence.getMostCurrentSensorData(getLastValidTimestamp());
        return sensorData.map(SensorData::getTimeStamp).orElse(initializationTimestamp);
    }

    private ZonedDateTime getLastValidTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION);
    }
}


