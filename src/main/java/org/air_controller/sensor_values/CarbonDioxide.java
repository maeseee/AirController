package org.air_controller.sensor_values;

import lombok.Getter;

@Getter
public class CarbonDioxide {

    private final double ppm;

    private CarbonDioxide(double ppm) {
        this.ppm = ppm;
    }

    @Override
    public String toString() {
        return String.format("%.0fppm", ppm);
    }

    public static CarbonDioxide createFromPpm(double ppm) throws InvalidArgumentException {
        if (ppm < 0.0 || ppm > 1000000.0) {
            throw new InvalidArgumentException("Given humidity of " + ppm + "% is out of range!");
        }
        return new CarbonDioxide(ppm);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarbonDioxide that)) return false;

        return Double.compare(ppm, that.ppm) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(ppm);
    }
}
