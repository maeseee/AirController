package org.air_controller.gpio.dingtian_relay;

import lombok.Getter;

public enum DingtianAction {
    OFF(0),
    ON(1);

    @Getter
    private final int index;

    DingtianAction(int index) {
        this.index = index;
    }

    public static DingtianAction from(boolean on) {
        return on ? DingtianAction.ON : DingtianAction.OFF;
    }
}
