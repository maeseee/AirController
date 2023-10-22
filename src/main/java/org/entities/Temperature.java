package org.entities;

public class Temperature {

    private static final double KELVIN_TO_CELSIUS = 273.15;

    private final double celsius;

    public Temperature(double celsius) {
        this.celsius = celsius;
    }

    public double getCelsius() {
        return celsius;
    }

    @Override
    public String toString() {
        return "TemperatureCelsius=" + celsius;
    }

    public static Temperature createTemperatureFromKelvin(double kelvin) {
        return new Temperature(kelvin - KELVIN_TO_CELSIUS);
    }
}
