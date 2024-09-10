package org.airController.rules;

import lombok.Getter;

public class Percentage {

    @Getter
    private final double percentage;

    public Percentage(double percentage) {
        this.percentage = Math.min(Math.max(percentage, -1), 1);
    }

    public Percentage(double percentage, double limiter) {
        if (Math.abs(limiter) > 1) {
            throw new IllegalArgumentException("limiter must be between -1 and 1");
        }
        this.percentage = Math.min(Math.max(percentage, -1), 1);
    }
}
