package org.airController.entities;

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
        return "Humidity=" + relativeHumidity + "%";
    }

    public static Humidity createFromRelative(double relativeHumidity) {
        return new Humidity(relativeHumidity);
    }

}
