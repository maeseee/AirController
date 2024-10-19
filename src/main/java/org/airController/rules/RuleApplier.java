package org.airController.rules;

import org.airController.system.OutputState;
import org.airController.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class RuleApplier implements Runnable {
    private static final Logger logger = LogManager.getLogger(RuleApplier.class);
    private static final double HYSTERESIS = 0.05;

    private final List<VentilationSystem> ventilationSystem;
    private final List<Rule> freshAirRules;
    private final List<Rule> exchangeHumidityRules;
    private OutputState airFlowState = OutputState.INITIALIZING; // The system for fresh air should be on by default
    private OutputState humidityExchangerState = OutputState.INITIALIZING; // The system to exchange humidity should be off by default

    public RuleApplier(List<VentilationSystem> ventilationSystem, List<Rule> freshAirRules, List<Rule> exchangeHumidityRules) {
        this.ventilationSystem = ventilationSystem;
        this.freshAirRules = freshAirRules;
        this.exchangeHumidityRules = exchangeHumidityRules;
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
        final double confidenceForFreshAir = getTotalConfidence(freshAirRules);
        final OutputState nextAirFlowState = nextStateWithHysteresis(confidenceForFreshAir, airFlowState);
        updateAirFlow(nextAirFlowState);

        final double confidenceForHumidityExchange = getTotalConfidence(exchangeHumidityRules);
        final OutputState nextHumidityExchangerState =
                nextAirFlowState.isOn() ? nextStateWithHysteresis(confidenceForHumidityExchange, humidityExchangerState) : OutputState.OFF;
        updateHumidityExchanger(nextHumidityExchangerState);
    }

    private double getTotalConfidence(List<Rule> rules) {
        return rules.stream()
                .mapToDouble(rule -> rule.turnOnConfidence().getWeightedConfidenceValue())
                .sum();
    }

    private OutputState nextStateWithHysteresis(double confidence, OutputState currentState) {
        confidence += (currentState.isOn()) ? HYSTERESIS : -HYSTERESIS;
        return confidence >= 0 ? OutputState.ON : OutputState.OFF;
    }


    private void updateAirFlow(OutputState nextAirFlowState) {
        if (airFlowState != nextAirFlowState) {
            ventilationSystem.forEach(system -> system.setAirFlowOn(nextAirFlowState));
            airFlowState = nextAirFlowState;
            final String ruleValues = freshAirRules.stream()
                    .map(rule -> String.format("%s: %.2f, ", rule.name(), rule.turnOnConfidence().getWeightedConfidenceValue()))
                    .collect(Collectors.joining());
            logger.info("Fresh air state changed to {} because of {}", nextAirFlowState, ruleValues);
        }
    }

    private void updateHumidityExchanger(OutputState nextHumidityExchangerState) {
        if (humidityExchangerState != nextHumidityExchangerState) {
            ventilationSystem.forEach(system -> system.setHumidityExchangerOn(nextHumidityExchangerState));
            humidityExchangerState = nextHumidityExchangerState;
        }
    }
}
