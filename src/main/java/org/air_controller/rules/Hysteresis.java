package org.air_controller.rules;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Hysteresis {

    private final double hysteresis;

    boolean changeStateWithHysteresis(double confidence, boolean isCurrentStatePositive) {
        double confidenceWithHysteresis = confidence + (isCurrentStatePositive ? hysteresis : -hysteresis);
        return confidenceWithHysteresis >= 0.0;
    }
}
