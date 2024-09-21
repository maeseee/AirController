package org.airController.entities;

import lombok.Getter;

@Getter
public class HumidityRelative {

    private final double relativeHumidity;

    private HumidityRelative(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    /**
     * @return absolute humidity in [g/m3]
     */
    public double getAbsoluteHumidity(Temperature temperature) {
        final double saturationVaporPressure = getSaturationVaporPressure(temperature);
        final double specificGasConstantForWater = 461.5; // [J/(kg*K)
        return relativeHumidity * saturationVaporPressure * 10 / (specificGasConstantForWater * temperature.getKelvin());
    }

    @Override
    public String toString() {
        return String.format("Humidity=%.2f%%", relativeHumidity);
    }

    public static HumidityRelative createFromRelative(double relativeHumidity) throws InvaildArgumentException {
        if (relativeHumidity < 0.0 || relativeHumidity > 100.0) {
            throw new InvaildArgumentException("Given humidity of " + relativeHumidity + "% is out of range!");
        }
        return new HumidityRelative(relativeHumidity);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HumidityRelative humidity)) return false;

        return Double.compare(relativeHumidity, humidity.relativeHumidity) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(relativeHumidity);
    }

    private static double getSaturationVaporPressure(Temperature temperature) {
        return 0.61078 * Math.exp(17.27 * temperature.getCelsius() / (temperature.getCelsius() + 237.3)) * 1000;
    }
}
