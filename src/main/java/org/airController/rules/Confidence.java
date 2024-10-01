package org.airController.rules;

import lombok.Getter;

@Getter
public class Confidence {

    private final double weightedConfidenceValue;

    public Confidence(double confidenceValue) {
        this(confidenceValue, 1.0);
    }

    public Confidence(double confidenceValue, double weight) {
        if (Math.abs(confidenceValue) > 1) {
            throw new IllegalArgumentException("confidenceValue must be between -1 and 1");
        }
        weightedConfidenceValue = confidenceValue * weight;
    }
}
