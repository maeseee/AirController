package org.airController.controllers;

import lombok.Setter;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Setter
public class CurrentSensorData implements SensorData {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    private Temperature temperature;
    private Humidity humidity;
    private CarbonDioxide co2;
    private LocalDateTime timestamp;

    @Override
    public Optional<Temperature> getTemperature() {
        return Optional.ofNullable(temperature);
    }

    @Override
    public Optional<Humidity> getHumidity() {
        return Optional.ofNullable(humidity);
    }

    public Optional<Double> getAbsoluteHumidity() {
        return Optional.of(humidity.getAbsoluteHumidity(temperature));
    }

    @Override
    public Optional<CarbonDioxide> getCo2() {
        return Optional.ofNullable(co2);
    }

    public void updateTimestamp() {
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSensorValid() {
        return LocalDateTime.now().minus(SENSOR_INVALIDATION).isBefore(timestamp);
    }
}
