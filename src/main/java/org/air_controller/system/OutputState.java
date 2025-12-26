package org.air_controller.system;

import lombok.Getter;

@Getter
public enum OutputState {
    ON(true),
    OFF(false),
    INITIALIZING(false);

    private final boolean isOn;

    OutputState(boolean isOn) {
        this.isOn = isOn;
    }

    public static OutputState fromIsOnState(boolean isOn) {
        return isOn ? ON : OFF;
    }
}
