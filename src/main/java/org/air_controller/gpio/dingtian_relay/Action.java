package org.air_controller.gpio.dingtian_relay;

import lombok.Getter;

@Getter
enum Action {
    OFF(0),
    ON(1);

    private final int index;

    Action(int index) {
        this.index = index;
    }

    public static Action from(boolean on) {
        return on ? Action.ON : Action.OFF;
    }
}
