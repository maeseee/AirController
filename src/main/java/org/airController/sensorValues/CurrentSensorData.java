package org.airController.sensorValues;

import com.google.inject.internal.Nullable;
import lombok.Setter;
import org.airController.sensor.SensorObserver;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Setter
public class CurrentSensorData implements SensorData, SensorObserver {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    private Temperature temperature;
    private Humidity humidity;
    @Nullable
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

    @Override
    public void updateSensorData(SensorData sensorData) {
        temperature = sensorData.getTemperature().orElse(null);
        humidity = sensorData.getHumidity().orElse(null);
        co2 = sensorData.getCo2().orElse(null);
        timestamp = sensorData.getTimeStamp();
    }

    private boolean isSensorValid() {
        return LocalDateTime.now().minus(SENSOR_INVALIDATION).isBefore(timestamp);
    }

    @Override
    public String toString() {
        return "CurrentSensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", co2=" + co2 +
                ", timestamp=" + timestamp +
                '}';
    }
}


