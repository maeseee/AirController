package org.airController.entities;

import java.util.Objects;

public class AirValue {
    private final Temperature temperature;
    private final Humidity humidity;

    public AirValue(Temperature temperature, Humidity humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Humidity getHumidity() {
        return humidity;
    }

    public double getAbsoluteHumidity() {
        return humidity.getAbsoluteHumidity(temperature);
    }

    @Override
    public String toString() {
        final String absoluteHumidity = String.format("Humidity=%.2fg/m3", getAbsoluteHumidity());
        return "AirValue{" + temperature + ", " + humidity + ", " + absoluteHumidity + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AirValue airValue = (AirValue) o;
        return Objects.equals(temperature, airValue.temperature) && Objects.equals(humidity, airValue.humidity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, humidity);
    }
}
