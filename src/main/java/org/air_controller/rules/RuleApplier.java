package org.air_controller.rules;

import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Consumer;

public class RuleApplier implements Runnable {
    private static final Logger logger = LogManager.getLogger(RuleApplier.class);

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
            logger.error("Exception in AirController loop:", exception);
        }
    }

    private void doRun() {
        airFlowSystem.updateSystemIfNecessary(true);
        humidityExchangerSystem.updateSystemIfNecessary(airFlowSystem.getCurrentState().isOn());
    }
}
