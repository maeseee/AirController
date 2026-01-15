package org.air_controller.rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.air_controller.system.OutputState;
import org.air_controller.system_action.VentilationSystemPersistenceData;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

@RequiredArgsConstructor
public class RuleApplierSystem {
    private static final double HYSTERESIS = 0.05;

    @Getter
    private OutputState currentState = OutputState.INITIALIZING;
    private final List<Rule> rules;
    private final Consumer<OutputState> updateAction;
    private final Consumer<VentilationSystemPersistenceData> updatePersistence;

    void updateSystemIfNecessary(boolean activated) {
        if (activated) {
            updateSystemIfNecessary();
        } else {
            updateSystem(OutputState.OFF);
            persistState(new VentilationSystemPersistenceData(OutputState.OFF, Double.NaN, emptyMap()));
        }
    }

    private void updateSystemIfNecessary() {
        final Hysteresis hysteresis = new Hysteresis(HYSTERESIS);
        final Map<String, Confidence> confidences = getEachConfidence();
        final double confidence = getTotalConfidence(confidences);
        final boolean nextStateOn = hysteresis.changeStateWithHysteresis(confidence, currentState.isOn());
        final OutputState action = OutputState.fromIsOnState(nextStateOn);
        updateSystem(action);
        persistState(new VentilationSystemPersistenceData(action, confidence, confidences));
    }

    private Map<String, Confidence> getEachConfidence() {
        return rules.stream().collect(
                Collectors.toMap(
                        Rule::name,
                        Rule::turnOnConfidence));
    }

    private double getTotalConfidence(Map<String, Confidence> confidences) {
        return confidences.values().stream()
                .mapToDouble(Confidence::getWeightedConfidenceValue)
                .sum();
    }

    private void updateSystem(OutputState nextState) {
        if (currentState != nextState) {
            updateAction.accept(nextState);
            currentState = nextState;
        }
    }

    private void persistState(VentilationSystemPersistenceData data) {
        updatePersistence.accept(data);
    }
}
