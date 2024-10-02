package org.airController.rules;

import lombok.Getter;

@Getter
public class Confidence {

    private final double weightedConfidenceValue;

    public Confidence(double confidenceValue) {
        this(confidenceValue, 1.0);
    }

    public Confidence(double confidenceValue, double weight) {
        weightedConfidenceValue = Math.max(Math.min(confidenceValue, 1.0), -1.0) * weight;
    }
}
