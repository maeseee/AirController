package org.airController.controllers;

import lombok.Setter;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalDouble;

public class CurrentSensorData implements SensorData {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    @Setter
    private Temperature temperature;
    @Setter
    private Humidity humidity;
    @Setter
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

    @Override
    public LocalDateTime getTimeStamp() {
        return timestamp;
    }

    public OptionalDouble getAbsoluteHumidity() {
        return isSensorValid() && humidity != null ?
                OptionalDouble.of(humidity.getAbsoluteHumidity()) :
                OptionalDouble.empty();
    }

    public void updateTimestamp() {
        this.timestamp = LocalDateTime.now();
    }

    private boolean isSensorValid() {
        return LocalDateTime.now().minus(SENSOR_INVALIDATION).isBefore(timestamp);
    }

    @Override public String toString() {
        return "CurrentSensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", co2=" + co2 +
                ", timestamp=" + timestamp +
                '}';
    }
}


