package org.air_controller.rules;

import lombok.Getter;

@Getter
public class Confidence {

    private final double weightedConfidenceValue;

    public Confidence(double confidenceValue, double weight) {
        weightedConfidenceValue = Math.max(Math.min(confidenceValue, 1.0), -1.0) * weight;
    }

    public String getWeightedConfidenceValueString() {
        return String.format("%.2f", weightedConfidenceValue);
    }
}
