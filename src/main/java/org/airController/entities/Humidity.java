package org.airController.entities;

import lombok.Getter;

@Getter
public class Humidity {
    private static final double SPECIFIC_GAS_CONSTANT_FOR_WATER = 461.5; // [J/(kg*K)]

    private final double absoluteHumidity; // [g/m3]

    private Humidity(double absoluteHumidity) {
        this.absoluteHumidity = absoluteHumidity;
    }

    public double getRelativeHumidity(Temperature temperature) {
        final double saturationVaporPressure = getSaturationVaporPressure(temperature);
        return absoluteHumidity * SPECIFIC_GAS_CONSTANT_FOR_WATER * temperature.getKelvin() / (saturationVaporPressure * 10);
    }

    @Override
    public String toString() {
        return String.format("%.2fg/m3", absoluteHumidity);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Humidity humidity)) return false;

        return Double.compare(absoluteHumidity, humidity.absoluteHumidity) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(absoluteHumidity);
    }

    public static Humidity createFromRelative(double relativeHumidity, Temperature temperature) throws InvaildArgumentException {
        if (relativeHumidity < 0.0 || relativeHumidity > 100.0) {
            throw new InvaildArgumentException("Given humidity of " + relativeHumidity + "% is out of range!");
        }
        return getAbsoluteHumidity(relativeHumidity, temperature);
    }

    public static Humidity createFromAbsolute(double absoluteHumidity) throws InvaildArgumentException {
        if (absoluteHumidity < 0.0) {
            throw new InvaildArgumentException("Given humidity of " + absoluteHumidity + "g/m3% is negativ!");
        }
        return new Humidity(absoluteHumidity);
    }

    private static Humidity getAbsoluteHumidity(double relativeHumidity, Temperature temperature) {
        final double saturationVaporPressure = getSaturationVaporPressure(temperature);
        final double absoluteHumidity =
                relativeHumidity * saturationVaporPressure * 1000 / (SPECIFIC_GAS_CONSTANT_FOR_WATER * temperature.getKelvin() * 100);
        return new Humidity(absoluteHumidity);
    }

    private static double getSaturationVaporPressure(Temperature temperature) {
        return 0.61078 * Math.exp(17.27 * temperature.getCelsius() / (temperature.getCelsius() + 237.3)) * 1000;
    }
}
