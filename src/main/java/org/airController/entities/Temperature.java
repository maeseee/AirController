package org.airController.entities;

import java.util.Objects;

public class Temperature {

    private static final double KELVIN_TO_CELSIUS = 273.15;

    private final double celsius;

    private Temperature(double celsius) {
        this.celsius = celsius;
    }

    public double getCelsius() {
        return celsius;
    }

    public double getKelvin() {
        return celsius + KELVIN_TO_CELSIUS;
    }

    @Override
    public String toString() {
        return "Temperature=" + celsius + "°C";
    }

    public static Temperature createFromCelsius(double celsius) {
        return new Temperature(celsius);
    }

    public static Temperature createFromKelvin(double kelvin) {
        return new Temperature(kelvin - KELVIN_TO_CELSIUS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temperature that = (Temperature) o;
        return Double.compare(celsius, that.celsius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(celsius);
    }
}
