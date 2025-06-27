package org.air_controller.system_action;

import lombok.RequiredArgsConstructor;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class SystemActionPersistence implements VentilationSystem {
    private final SystemActionDbAccessors dbAccessors;

    @Override
    public void setAirFlowOn(OutputState state) {
        final Optional<SystemAction> currentAction = dbAccessors.airFlow().getMostCurrentState();
        if (currentAction.isPresent() && currentAction.get().outputState() == state) {
            return;
        }
        dbAccessors.airFlow().insertAction(state, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
        dbAccessors.humidity().insertAction(state, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public OutputState isAirFlowOn() {
        return dbAccessors.airFlow()
                .getMostCurrentState()
                .map(SystemAction::outputState)
                .orElse(OutputState.INITIALIZING);
    }
}
