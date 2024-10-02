package org.airController.system;

public enum OutputState {
    ON(true),
    OFF(false),
    INITIALIZING(false);

    private final boolean isOn;

    OutputState(boolean isOn) {
        this.isOn = isOn;
    }

    public boolean isOn() {
        return isOn;
    }
}
