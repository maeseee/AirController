package org.air_controller.system_action;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VentilationSystemPersistence {
    private final VentilationSystemDbAccessors dbAccessors;

    public void persistAirFlowData(VentilationSystemPersistenceData data) {
        dbAccessors.airFlow().insertAction(data);
    }

    public void persistHumidityExchangerData(VentilationSystemPersistenceData data) {
        dbAccessors.humidity().insertAction(data);
    }
}
