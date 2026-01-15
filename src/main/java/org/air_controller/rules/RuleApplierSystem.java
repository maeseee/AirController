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
    private OutputState currentState = OutputState.INITIALIZING;
    private final List<Rule> rules;
    private final Consumer<OutputState> updateAction;
    private final Consumer<OutputState> updatePersistence;

    void updateSystemIfNecessary(boolean activated) {
        if (activated) {
            updateSystemIfNecessary();
        } else {
            updateSystem(OutputState.OFF);
        }
    }

    private void updateSystemIfNecessary() {
        final Hysteresis hysteresis = new Hysteresis(HYSTERESIS);
        final double confidence = getTotalConfidence(rules);
        boolean nextStateOn = hysteresis.changeStateWithHysteresis(confidence, currentState.isOn());
        updateSystem(OutputState.fromIsOnState(nextStateOn));
    }

    private double getTotalConfidence(List<Rule> rules) {
        return rules.stream()
                .mapToDouble(rule -> rule.turnOnConfidence().getWeightedConfidenceValue())
                .sum();
    }

    private void updateSystem(OutputState nextState) {
        if (currentState != nextState) {
            updateAction.accept(nextState);
            updatePersistence.accept(nextState);
            currentState = nextState;
        }
    }
}
