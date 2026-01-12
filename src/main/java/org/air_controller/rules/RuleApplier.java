package org.air_controller.rules;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class RuleApplier implements Runnable {
    private final RuleApplierSystem airFlowSystem;
    private final RuleApplierSystem humidityExchangerSystem;

    public RuleApplier(List<VentilationSystem> ventilationSystems, List<Rule> freshAirRules, List<Rule> humidityExchangerRules) {
        final Consumer<OutputState> airFlowUpdateAction =
                (OutputState nextState) -> ventilationSystems.forEach(ventilationSystem -> ventilationSystem.setAirFlowOn(nextState));
        this.airFlowSystem = new RuleApplierSystem(freshAirRules, airFlowUpdateAction);

        final Consumer<OutputState> humidityExchangerAction =
                (OutputState nextState) -> ventilationSystems.forEach(ventilationSystem -> ventilationSystem.setHumidityExchangerOn(nextState));
        this.humidityExchangerSystem = new RuleApplierSystem(humidityExchangerRules, humidityExchangerAction);
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            log.error("Exception in AirController loop:", exception);
        }
    }

    private void doRun() {
        airFlowSystem.updateSystemIfNecessary(true);
        humidityExchangerSystem.updateSystemIfNecessary(airFlowSystem.getCurrentState().isOn());
    }
}
