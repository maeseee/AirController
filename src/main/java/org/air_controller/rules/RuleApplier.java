package org.air_controller.rules;

import lombok.RequiredArgsConstructor;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@RequiredArgsConstructor
public class RuleApplier implements Runnable {
    private static final Logger logger = LogManager.getLogger(RuleApplier.class);
    private static final double HYSTERESIS = 0.05;
    private static final double ON_CONFIDENCE = 0.1;

    private final List<VentilationSystem> ventilationSystem;
    private final List<Rule> freshAirRules;
    private final List<Rule> exchangeHumidityRules;
    private OutputState airFlowState = OutputState.INITIALIZING; // The system for fresh air should be on by default
    private OutputState humidityExchangerState = OutputState.INITIALIZING; // The system to exchange humidity should be off by default

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
        return confidence >= ON_CONFIDENCE ? OutputState.ON : OutputState.OFF;
    }


    private void updateAirFlow(OutputState nextAirFlowState) {
        if (airFlowState != nextAirFlowState) {
            ventilationSystem.forEach(system -> system.setAirFlowOn(nextAirFlowState));
            airFlowState = nextAirFlowState;
        }
    }

    private void updateHumidityExchanger(OutputState nextHumidityExchangerState) {
        if (humidityExchangerState != nextHumidityExchangerState) {
            ventilationSystem.forEach(system -> system.setHumidityExchangerOn(nextHumidityExchangerState));
            humidityExchangerState = nextHumidityExchangerState;
        }
    }
}
