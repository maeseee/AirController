package org.air_controller.system_action;

import lombok.RequiredArgsConstructor;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class VentilationSystemPersistence {
    private final VentilationSystemDbAccessors dbAccessors;

    public void persistAirFlowData(VentilationSystemPersistenceData data) {
        final Optional<SystemAction> currentAction = dbAccessors.airFlow().getMostCurrentState();
        if (currentAction.isPresent() && currentAction.get().outputState() == data.action()) {
            return;
        }
        dbAccessors.airFlow().insertAction(data.action(), ZonedDateTime.now(ZoneOffset.UTC));
    }

    public void persistHumidityExchangerData(VentilationSystemPersistenceData data) {
        dbAccessors.humidity().insertAction(data.action(), ZonedDateTime.now(ZoneOffset.UTC));
    }
}
