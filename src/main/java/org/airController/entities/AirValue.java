package org.airController.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class AirValue {
    private final Temperature temperature;
    private final Humidity humidity;
    private final CarbonDioxide co2;
    private final LocalDateTime timeStamp;

    public AirValue(Temperature temperature, Humidity humidity) {
        this(temperature, humidity, null, LocalDateTime.now());
    }

    public AirValue(Temperature temperature, Humidity humidity, LocalDateTime timeStamp) {
        this(temperature, humidity, null, timeStamp);
    }

    public AirValue(Temperature temperature, Humidity humidity, CarbonDioxide co2, LocalDateTime timeStamp) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
        this.timeStamp = timeStamp;
    }

    public Temperature getTemperature() {

        return temperature;
    }

    public Humidity getHumidity() {

        return humidity;
    }

    public Optional<CarbonDioxide> getCo2() {
        return Optional.ofNullable(co2);
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public double getAbsoluteHumidity() {
        return humidity.getAbsoluteHumidity(temperature);
    }

    @Override
    public String toString() {
        final String absoluteHumidity = String.format("Humidity=%.2fg/m3", getAbsoluteHumidity());
        return "AirValue{" + temperature + ", " + humidity + ", " + absoluteHumidity + ", " + co2 + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AirValue airValue = (AirValue) o;
        return Objects.equals(temperature, airValue.temperature) &&
                Objects.equals(humidity, airValue.humidity) &&
                Objects.equals(co2, airValue.co2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, humidity);
    }
}
