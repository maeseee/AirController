package org.air_controller.sensor_values;

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
        return String.format("%.2f°C", celsius);
    }

    public static Temperature createFromCelsius(double celsius) throws InvalidArgumentException {
        validateTemperature(celsius);
        return new Temperature(celsius);
    }

    public static Temperature createFromKelvin(double kelvin) throws InvalidArgumentException {
        final double celsius = kelvin - KELVIN_TO_CELSIUS;
        validateTemperature(celsius);
        return new Temperature(celsius);
    }

    private static void validateTemperature(double celsius) throws InvalidArgumentException {
        if (celsius < -60.0 || celsius > 100.0) {
            throw new InvalidArgumentException("Given temperature of " + celsius + "°C is unrealistic!");
        }
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
