package org.air_controller.system_action;

import lombok.Getter;

@Getter
public enum SystemPart {
    AIR_FLOW("airFlowActions"),
    HUMIDITY("humidityActions");

    private final String tableName;

    SystemPart(String tableName) {
        this.tableName = tableName;
    }
}
