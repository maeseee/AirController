package org.airController.entities;

import java.io.IOException;
import java.util.Objects;

public class CarbonDioxide {

    private final double ppm;

    private CarbonDioxide(double ppm) {
        this.ppm = ppm;
    }

    public double getPpm() {
        return ppm;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarbonDioxide that = (CarbonDioxide) o;
        return Double.compare(ppm, that.ppm) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ppm);
    }
}
