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
    private LocalDateTime timestamp = LocalDateTime.now();

    @Override
    public Optional<Temperature> getTemperature() {
        return isSensorValid() ?
                Optional.ofNullable(temperature) :
                Optional.empty();
    }

    @Override
    public Optional<Humidity> getHumidity() {
        return isSensorValid() ?
                Optional.ofNullable(humidity) :
                Optional.empty();
    }

    @Override
    public Optional<CarbonDioxide> getCo2() {
        return isSensorValid() ?
                Optional.ofNullable(co2) :
                Optional.empty();
    }

    public Optional<Double> getAbsoluteHumidity() {
        return canAbsoluteHumidityBeCalculated() ?
                Optional.of(humidity.getAbsoluteHumidity(temperature)) :
                Optional.empty();
    }

    public void updateTimestamp() {
        this.timestamp = LocalDateTime.now();
    }

    private boolean canAbsoluteHumidityBeCalculated() {
        return isSensorValid() && temperature != null && humidity != null;
    }

    private boolean isSensorValid() {
        return LocalDateTime.now().minus(SENSOR_INVALIDATION).isBefore(timestamp);
    }
}
