package org.air_controller.sensor_values;

import org.jetbrains.annotations.NotNull;

public record Temperature(double celsius) {

    public Temperature {
        validate(celsius, IllegalArgumentException.class);
    }

    private static final double KELVIN_TO_CELSIUS = 273.15;

    public double getKelvin() {
        return celsius + KELVIN_TO_CELSIUS;
    }

    @Override
    public @NotNull String toString() {
        return String.format("%.2f°C", celsius);
    }

    public static Temperature createFromCelsius(double celsius) throws InvalidArgumentException {
        validate(celsius, InvalidArgumentException.class);
        return new Temperature(celsius);
    }

    public static Temperature createFromKelvin(double kelvin) throws InvalidArgumentException {
        final double celsius = kelvin - KELVIN_TO_CELSIUS;
        validate(celsius, InvalidArgumentException.class);
        return new Temperature(celsius);
    }

    private static <T extends Exception> void validate(double celsius, Class<T> exceptionClass) throws T {
        if (celsius < -60.0 || celsius > 100.0) {
            final String message = "Given temperature of " + celsius + "°C is unrealistic!";
            try {
                throw exceptionClass.getConstructor(String.class).newInstance(message);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to create exception of type " + exceptionClass.getName(), e);
            }
        }
    }
}
