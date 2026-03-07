package org.air_controller.rules;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Hysteresis {

    private final double hysteresis;

    public boolean changeStateWithHysteresis(double confidence, boolean isCurrentStatePositive) {
        double confidenceWithHysteresis = confidence + (isCurrentStatePositive ? hysteresis : -hysteresis);
        return confidenceWithHysteresis >= 0.0;
    }
}
