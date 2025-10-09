package org.air_controller.sensor_values;

import org.jetbrains.annotations.NotNull;

public record CarbonDioxide(double ppm) {

    @Override
    public @NotNull String toString() {
        return String.format("%.0fppm", ppm);
    }

    public static CarbonDioxide createFromPpm(double ppm) throws InvalidArgumentException {
        if (ppm < 0.0 || ppm > 1000000.0) {
            throw new InvalidArgumentException("Given humidity of " + ppm + "% is out of range!");
        }
        return new CarbonDioxide(ppm);
    }
}
