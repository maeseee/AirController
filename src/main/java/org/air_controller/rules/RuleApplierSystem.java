package org.air_controller.rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.air_controller.system.OutputState;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class RuleApplierSystem {
    private static final double HYSTERESIS = 0.05;

    @Getter
    private OutputState currentState = OutputState.INITIALIZING; // The system for fresh air should be on by default
    private final List<Rule> rules;
    private final Consumer<OutputState> updateAction;

    void updateSystemIfNecessary() {
        final Hysteresis hysteresis = new Hysteresis(HYSTERESIS);

        final double confidenceForFreshAir = getTotalConfidence(rules);
        boolean nextAirFlowStateOn = hysteresis.changeStateWithHysteresis(confidenceForFreshAir, currentState.isOn());
        updateSystem(OutputState.fromIsOnState(nextAirFlowStateOn));
    }

    private double getTotalConfidence(List<Rule> rules) {
        return rules.stream()
                .mapToDouble(rule -> rule.turnOnConfidence().getWeightedConfidenceValue())
                .sum();
    }

    private void updateSystem(OutputState nextAirFlowState) {
        if (currentState != nextAirFlowState) {
            updateAction.accept(nextAirFlowState);
            currentState = nextAirFlowState;
        }
    }
}
