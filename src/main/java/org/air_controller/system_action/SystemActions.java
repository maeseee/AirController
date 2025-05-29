package org.air_controller.system_action;

import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;

import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SystemActions implements VentilationSystem {
    private final SystemActionDbAccessor airFlowDbAccessor;
    private final SystemActionDbAccessor humidityExchangerDbAccessor;

    public SystemActions(SystemActionDbAccessor airFlowDbAccessor, SystemActionDbAccessor humidityExchangerDbAccessor) throws SQLException {
        this.airFlowDbAccessor = airFlowDbAccessor;
        this.humidityExchangerDbAccessor = humidityExchangerDbAccessor;
    }

    @Override
    public void setAirFlowOn(OutputState state) {
        final Optional<SystemAction> currentAction = airFlowDbAccessor.getMostCurrentState();
        if (currentAction.isPresent() && currentAction.get().outputState() == state) {
            return;
        }
        airFlowDbAccessor.insertAction(state, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
        humidityExchangerDbAccessor.insertAction(state, ZonedDateTime.now(ZoneOffset.UTC));
    }

    public List<SystemAction> getAirFlowActionsFromTimeToNow(ZonedDateTime startTime, SystemPart systemPart) {
        return airFlowDbAccessor.getActionsFromTimeToNow(startTime, systemPart);
    }
}
