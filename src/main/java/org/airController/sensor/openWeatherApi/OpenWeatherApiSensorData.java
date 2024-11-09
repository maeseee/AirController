package org.airController.sensor.openWeatherApi;

import org.airController.sensorValues.CarbonDioxide;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.SensorData;
import org.airController.sensorValues.Temperature;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

class OpenWeatherApiSensorData implements SensorData {

    private final Temperature temperature;
    private final Humidity humidity;
    private final ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("UTC"));

    public OpenWeatherApiSensorData(Temperature temperature, Humidity humidity) {
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
        return "OpenWeatherApiSensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", timestamp=" + timestamp +
                '}';
    }
}
