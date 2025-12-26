package org.air_controller.rules;

import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Consumer;

public class RuleApplier implements Runnable {
    private static final Logger logger = LogManager.getLogger(RuleApplier.class);
    private static final double HYSTERESIS = 0.05;

    private final RuleApplierSystem airFlowSystem;

    private final List<Rule> humidityExchangerRules;
    private OutputState humidityExchangerState = OutputState.INITIALIZING; // The system to exchange humidity should be off by default
    private final Consumer<OutputState> humidityExchangerAction;

    public RuleApplier(List<VentilationSystem> ventilationSystems, List<Rule> freshAirRules, List<Rule> humidityExchangerRules) {
        final Consumer<OutputState> airFlowUpdateAction =
                (OutputState nextState) -> ventilationSystems.forEach(ventilationSystem -> ventilationSystem.setAirFlowOn(nextState));
        this.airFlowSystem = new RuleApplierSystem(freshAirRules, airFlowUpdateAction);

        this.humidityExchangerRules = humidityExchangerRules;
        this.humidityExchangerAction =
                (OutputState nextState) -> ventilationSystems.forEach(ventilationSystem -> ventilationSystem.setHumidityExchangerOn(nextState));
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
        final Hysteresis hysteresis = new Hysteresis(HYSTERESIS);

        airFlowSystem.updateSystemIfNecessary();

        final double confidenceForHumidityExchange = getTotalConfidence(humidityExchangerRules);
        boolean nextHumidityExchangerStateOn = hysteresis.changeStateWithHysteresis(confidenceForHumidityExchange, humidityExchangerState.isOn());
        final OutputState nextHumidityExchangerState = OutputState.fromIsOnState(airFlowSystem.getCurrentState().isOn() && nextHumidityExchangerStateOn);
        updateHumidityExchanger(nextHumidityExchangerState);
    }

    private double getTotalConfidence(List<Rule> rules) {
        return rules.stream()
                .mapToDouble(rule -> rule.turnOnConfidence().getWeightedConfidenceValue())
                .sum();
    }

    private void updateHumidityExchanger(OutputState nextHumidityExchangerState) {
        if (humidityExchangerState != nextHumidityExchangerState) {
            humidityExchangerAction.accept(nextHumidityExchangerState);
            humidityExchangerState = nextHumidityExchangerState;
        }
    }
}
