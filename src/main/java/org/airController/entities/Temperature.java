package org.airController.entities;

import lombok.Getter;

@Getter
public class Temperature {

    private static final double KELVIN_TO_CELSIUS = 273.15;

    private final double celsius;

    private Temperature(double celsius) {
        this.celsius = celsius;
    }

    public double getKelvin() {
        return celsius + KELVIN_TO_CELSIUS;
    }

    @Override
    public String toString() {
        return String.format("%.2f°C", +celsius);
    }

    public static Temperature createFromCelsius(double celsius) {
        return new Temperature(celsius);
    }

    public static Temperature createFromKelvin(double kelvin) {
        return new Temperature(kelvin - KELVIN_TO_CELSIUS);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Temperature that)) return false;

        return Double.compare(celsius, that.celsius) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(celsius);
    }
}
