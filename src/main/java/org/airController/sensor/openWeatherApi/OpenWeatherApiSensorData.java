package org.airController.sensor.openWeatherApi;

import org.airController.controllers.SensorData;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.util.Optional;

public class OpenWeatherApiSensorData implements SensorData {

    private final Temperature temperature;
    private final Humidity humidity;

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
}