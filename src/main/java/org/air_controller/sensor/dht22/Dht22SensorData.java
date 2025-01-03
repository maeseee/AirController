package org.air_controller.sensor.dht22;

import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.SensorData;
import org.air_controller.sensor_values.Temperature;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

public class Dht22SensorData implements SensorData {

    private final Temperature temperature;
    private final Humidity humidity;
    private final ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

    public Dht22SensorData(Temperature temperature, Humidity humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
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
        return Optional.empty();
    }

    @Override
    public ZonedDateTime getTimeStamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Dht22SensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", timestamp=" + timestamp +
                '}';
    }
}
