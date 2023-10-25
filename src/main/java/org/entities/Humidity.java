package org.entities;

public class Humidity {

    private final double relativeHumidity;

    private Humidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    /**
     * @return absulute humdidity in [g/m3]
     */
    public double getAbsoluteHumidity(Temperature temperature) {
        // function calculated for
        // Temperatur [°C]  water for 100 % [g/m3]
        // -20              0.9
        // -15	            1.4
        // -10	            2.1
        //  -5	            3.3
        //   0	            4.8
        //   5	            6.8
        //  10	            9.4
        //  15	           12.8
        //  20	           17.3
        //  25	           23.0
        //  30	           30.3
        //  35	           39.6
        //  40	           51.1

        return (4.2431796244 * Math.exp(0.0666427637 * temperature.getCelsius()) * relativeHumidity / 100.0);
    }

    @Override
    public String toString() {
        return "relativeHumidity=" + relativeHumidity;
    }

    public static Humidity createFromRelative(double relativeHumidity) {
        return new Humidity(relativeHumidity);
    }

}
