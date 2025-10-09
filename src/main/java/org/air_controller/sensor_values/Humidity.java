package org.air_controller.sensor_values;

import org.jetbrains.annotations.NotNull;

/**
 * @param absoluteHumidity [g/m3]
 */
public record Humidity(double absoluteHumidity) {

    public Humidity {
        validate(absoluteHumidity, IllegalArgumentException.class);
    }

    private static final double SPECIFIC_GAS_CONSTANT_FOR_WATER = 461.5; // [J/(kg*K)]

    public double getRelativeHumidity(Temperature temperature) {
        final double saturationVaporPressure = getSaturationVaporPressure(temperature);
        return absoluteHumidity * SPECIFIC_GAS_CONSTANT_FOR_WATER * temperature.getKelvin() / (saturationVaporPressure * 10);
    }

    @Override
    public @NotNull String toString() {
        return String.format("%.2fg/m3", absoluteHumidity);
    }

    public static Humidity createFromRelative(double relativeHumidity, Temperature temperature) throws InvalidArgumentException {
        if (relativeHumidity < 0.0 || relativeHumidity > 100.0) {
            throw new InvalidArgumentException("Given humidity of " + relativeHumidity + "% is out of range!");
        }
        return getAbsoluteHumidity(relativeHumidity, temperature);
    }

    public static Humidity createFromAbsolute(double absoluteHumidity) throws InvalidArgumentException {
        validate(absoluteHumidity, InvalidArgumentException.class);
        return new Humidity(absoluteHumidity);
    }

    private static Humidity getAbsoluteHumidity(double relativeHumidity, Temperature temperature) {
        final double saturationVaporPressure = getSaturationVaporPressure(temperature);
        final double absoluteHumidity =
                relativeHumidity * saturationVaporPressure * 1000 / (SPECIFIC_GAS_CONSTANT_FOR_WATER * temperature.getKelvin() * 100);
        return new Humidity(absoluteHumidity);
    }

    private static double getSaturationVaporPressure(Temperature temperature) {
        return 0.61078 * Math.exp(17.27 * temperature.celsius() / (temperature.celsius() + 237.3)) * 1000;
    }

    private static <T extends Exception> void validate(double absoluteHumidity, Class<T> exceptionClass) throws T {
        if (absoluteHumidity < 0.0) {
            final String message = "Given humidity of " + absoluteHumidity + "g/m3% is negativ!";
            try {
                throw exceptionClass.getConstructor(String.class).newInstance(message);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to create exception of type " + exceptionClass.getName(), e);
            }
        }
    }
}
