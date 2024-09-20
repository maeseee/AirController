package org.airController.sensor.qingPing;

import org.airController.controllers.SensorData;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.time.LocalDateTime;
import java.util.Optional;

public class QingPingSensorData implements SensorData {

    private final Temperature temperature;
    private final Humidity humidity;
    private final CarbonDioxide co2;
    private final LocalDateTime timestamp;

    public QingPingSensorData(Temperature temperature, Humidity humidity, CarbonDioxide co2, LocalDateTime timestamp) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
        this.timestamp = timestamp;
    }

    @Override
    public Optional<Temperature> getTemperature() {
        return Optional.of(temperature);
    }

    @Override
    public Optional<Humidity> getHumidity() {
        return Optional.of(humidity);
    }

    @Override
    public Optional<CarbonDioxide> getCo2() {
        return Optional.of(co2);
    }

    public LocalDateTime getTimeStamp() {
        return timestamp;
    }
}
