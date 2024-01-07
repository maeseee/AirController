package org.airController.entities;

import java.io.IOException;
import java.util.Objects;

public class Humidity {

    private final double relativeHumidity;

    private Humidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    /**
     * @return absolute humidity in [%]
     */
    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    /**
     * @return absolute humidity in [g/m3]
     */
    public double getAbsoluteHumidity(Temperature temperature) {
        final double saturationVaporPressure =
                0.61078 * Math.exp(17.27 * temperature.getCelsius() / (temperature.getCelsius() + 237.3)) * 1000; // Tetens equation [Pa]
        final double specificGasConstantForWater = 461.5; // [J/(kg*K)
        return relativeHumidity * saturationVaporPressure * 1000 / (specificGasConstantForWater * temperature.getKelvin() * 100);
    }

    @Override
    public String toString() {
        return String.format("Humidity=%.2f%%", relativeHumidity);
    }

    public static Humidity createFromRelative(double relativeHumidity) throws IOException {
        if (relativeHumidity < 0.0 || relativeHumidity > 100.0) {
            throw new IOException();
        }
        return new Humidity(relativeHumidity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Humidity humidity = (Humidity) o;
        return Double.compare(relativeHumidity, humidity.relativeHumidity) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(relativeHumidity);
    }
}
