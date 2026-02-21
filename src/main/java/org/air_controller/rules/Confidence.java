package org.air_controller.rules;

public record Confidence(double value) {

    public static Confidence createWeighted(double value, double weight) {
        return new Confidence(Math.max(Math.min(value, 1.0), -1.0) * weight);
    }

    public static Confidence createEmpty() {
        return new Confidence(0.0);
    }

    public Confidence invertConfidence() {
        return new Confidence(-value);
    }
}
