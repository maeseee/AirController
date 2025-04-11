package org.air_controller.gpio.dingtian_relay;

import lombok.Getter;

@Getter
public enum DingtianRelay {
    AIR_FLOW(0),
    HUMIDITY_EXCHANGER(1),
    NIGHT_TIME(2),
    RESERVE(3);

    private final int relayIndex;

    DingtianRelay(int relayIndex) {
        this.relayIndex = relayIndex;
    }
}
