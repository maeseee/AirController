package org.airController.rules;

import lombok.Getter;

public class Percentage {

    @Getter
    private final double percentage;

    public Percentage(double percentage) {
        this(percentage, -1, 1);
    }

    public Percentage(double percentage, double lowerBound, double upperBound) {
        if (lowerBound < -1 || upperBound > 1 || lowerBound > upperBound) {
            throw new IllegalArgumentException("bounds must be between -1 and 1");
        }
        this.percentage = Math.min(Math.max(percentage, lowerBound), upperBound);
    }
}
