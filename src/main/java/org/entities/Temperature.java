package org.entities;

public class Temperature {
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
}
