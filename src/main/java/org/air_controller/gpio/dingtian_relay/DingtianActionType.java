package org.air_controller.gpio.dingtian_relay;

import lombok.Getter;

@Getter
enum DingtianActionType {
    ON_OFF(0),
    JOGGING(1),
    DELAY(2),
    FLASH(3),
    TOGGLE(4);

    private final int index;

    DingtianActionType(int index) {
        this.index = index;
    }
}
