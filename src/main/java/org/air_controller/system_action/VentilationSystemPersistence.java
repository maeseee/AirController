package org.air_controller.system_action;

import lombok.RequiredArgsConstructor;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
public class VentilationSystemPersistence {
    private final VentilationSystemDbAccessors dbAccessors;

    public void persistAirFlowData(VentilationSystemPersistenceData data) {
        dbAccessors.airFlow().insertAction(data, ZonedDateTime.now(ZoneOffset.UTC));
    }

    public void persistHumidityExchangerData(VentilationSystemPersistenceData data) {
        dbAccessors.humidity().insertAction(data, ZonedDateTime.now(ZoneOffset.UTC));
    }
}
