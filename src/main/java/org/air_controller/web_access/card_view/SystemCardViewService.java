package org.air_controller.web_access.card_view;

import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class SystemCardViewService implements InterfaceService {

    private final SystemActionDbAccessor airFlowDbAccessor;

    public SystemCardViewService(SystemActionDbAccessor airFlowDbAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
    }

    @Override
    public CardView getCardView() {
        final Optional<SystemAction> systemAction = airFlowDbAccessor.getMostCurrentSystemAction();
        final String systemState = systemAction.map(SystemAction::outputState).map(outputState -> outputState.isOn() ? "ON" : "OFF").orElse("UNKNOWN");
        final CardItem systemStateItem = new CardItem("System State", systemState, "");
        return new CardView("", List.of(systemStateItem));
    }
}
