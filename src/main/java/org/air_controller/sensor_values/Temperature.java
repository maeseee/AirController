package org.air_controller.sensor_values;

import org.jetbrains.annotations.NotNull;

public record Temperature(double celsius) {

    private static final double KELVIN_TO_CELSIUS = 273.15;

    public double getKelvin() {
        return celsius + KELVIN_TO_CELSIUS;
    }

    @Override
    public @NotNull String toString() {
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
}
