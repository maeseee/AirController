package org.air_controller.sensor_values;

import java.time.ZonedDateTime;
import java.util.Optional;

public class SensorDataBuilder {

    private Temperature temperature;
    private Humidity humidity;
    private CarbonDioxide co2;
    private ZonedDateTime time;

    public SensorDataBuilder setTemperature(Temperature temperature) {
        this.temperature = temperature;
        return this;
    }

    public SensorDataBuilder setTemperatureCelsius(double tempCelsius) throws InvalidArgumentException {
        this.temperature = Temperature.createFromCelsius(tempCelsius);
        return this;
    }

    public SensorDataBuilder setTemperatureKelvin(double tempCelsius) throws InvalidArgumentException {
        this.temperature = Temperature.createFromKelvin(tempCelsius);
        return this;
    }

    public SensorDataBuilder setHumidity(Humidity humidity) {
        this.humidity = humidity;
        return this;
    }

    public SensorDataBuilder setHumidityAbsolute(double humAbsolute) throws InvalidArgumentException {
        this.humidity = Humidity.createFromAbsolute(humAbsolute);
        return this;
    }

    public SensorDataBuilder setHumidityRelative(double humRelative) throws InvalidArgumentException {
        if (temperature == null) {
            throw new InvalidArgumentException("Temperature must have a value when setting the relativ humidity");
        }
        this.humidity = Humidity.createFromRelative(humRelative, temperature);
        return this;
    }

    public SensorDataBuilder setCo2(CarbonDioxide co2) {
        this.co2 = co2;
        return this;
    }

    public SensorDataBuilder setCo2(Double co2Ppm) throws InvalidArgumentException {
        this.co2 = co2Ppm != null ? CarbonDioxide.createFromPpm(co2Ppm) : null;
        return this;
    }

    public SensorDataBuilder setTime(ZonedDateTime time) {
        this.time = time;
        return this;
    }

    public SensorData build() {
        validateNecessaryFieldSet();
        if (this.time == null) {
            // TODO set time in constructor instead of reading it from the external sources!
            this.time = ZonedDateTime.now();
        }
        return new SensorData(temperature, humidity, Optional.ofNullable(co2), time);
    }

    private void validateNecessaryFieldSet() {
        if (this.temperature == null || this.humidity == null) {
            throw new IllegalArgumentException("Temperature or Humidity is not set in the builder!");
        }
    }
}
