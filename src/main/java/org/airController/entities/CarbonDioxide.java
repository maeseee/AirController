package org.airController.entities;

import lombok.Getter;

import java.io.IOException;

@Getter
public class CarbonDioxide {

    private final double ppm;

    private CarbonDioxide(double ppm) {
        this.ppm = ppm;
    }

    @Override
    public String toString() {
        return String.format("CO2=%.0fppm", ppm);
    }

    public static CarbonDioxide createFromPpm(double ppm) throws IOException {
        if (ppm < 0.0 || ppm > 1000000.0) {
            throw new IOException();
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
