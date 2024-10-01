package org.airController.rules;

import lombok.Getter;

@Getter
public class Confident {

    private final double weightedConfidentValue;

    public Confident(double confidentValue) {
        this(confidentValue, 1.0);
    }

    public Confident(double confidentValue, double weight) {
        if (Math.abs(confidentValue) > 1) {
            throw new IllegalArgumentException("confidentValue must be between -1 and 1");
        }
        weightedConfidentValue = confidentValue * weight;
    }
}
