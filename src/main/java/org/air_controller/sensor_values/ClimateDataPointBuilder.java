package org.air_controller.sensor_values;

import java.time.ZonedDateTime;
import java.util.Optional;

public class ClimateDataPointBuilder {

    private Temperature temperature;
    private Humidity humidity;
    private CarbonDioxide co2;
    private ZonedDateTime time;

    public ClimateDataPointBuilder setTemperature(Temperature temperature) {
        this.temperature = temperature;
        return this;
    }

    public ClimateDataPointBuilder setTemperatureCelsius(double tempCelsius) throws InvalidArgumentException {
        this.temperature = Temperature.createFromCelsius(tempCelsius);
        return this;
    }

    public ClimateDataPointBuilder setTemperatureKelvin(double tempCelsius) throws InvalidArgumentException {
        this.temperature = Temperature.createFromKelvin(tempCelsius);
        return this;
    }

    public ClimateDataPointBuilder setHumidity(Humidity humidity) {
        this.humidity = humidity;
        return this;
    }

    public ClimateDataPointBuilder setHumidityAbsolute(double humAbsolute) throws InvalidArgumentException {
        this.humidity = Humidity.createFromAbsolute(humAbsolute);
        return this;
    }

    public ClimateDataPointBuilder setHumidityRelative(double humRelative) throws InvalidArgumentException {
        if (temperature == null) {
            throw new InvalidArgumentException("Temperature must have a value when setting the relativ humidity");
        }
        this.humidity = Humidity.createFromRelative(humRelative, temperature);
        return this;
    }

    public ClimateDataPointBuilder setCo2(CarbonDioxide co2) {
        this.co2 = co2;
        return this;
    }

    public ClimateDataPointBuilder setCo2(Double co2Ppm) throws InvalidArgumentException {
        this.co2 = co2Ppm != null ? CarbonDioxide.createFromPpm(co2Ppm) : null;
        return this;
    }

    public ClimateDataPointBuilder setTime(ZonedDateTime time) {
        this.time = time;
        return this;
    }

    public ClimateDataPoint build() {
        validateNecessaryFieldSet();
        if (this.time == null) {
            // TODO set time in constructor instead of reading it from the external sources!
            this.time = ZonedDateTime.now();
        }
        return new ClimateDataPoint(temperature, humidity, Optional.ofNullable(co2), time);
    }

    private void validateNecessaryFieldSet() {
        if (this.temperature == null || this.humidity == null) {
            throw new IllegalArgumentException("Temperature or Humidity is not set in the builder!");
        }
    }
}
