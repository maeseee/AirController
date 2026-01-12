package org.air_controller.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.air_controller.rules.Rule;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class SystemStateLogger implements Runnable {
    private final VentilationSystem ventilationSystem;
    private final List<Rule> freshAirRules;

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            log.error("Exception in AirController loop:", exception);
        }
    }

    private void doRun() {
        final OutputState state = ventilationSystem.isAirFlowOn();
        final double confidenceScore = freshAirRules.stream()
                .mapToDouble(rule -> rule.turnOnConfidence().getWeightedConfidenceValue())
                .sum();
        final String ruleValues = freshAirRules.stream()
                .map(rule -> String.format("%s: %.2f, ", rule.name(), rule.turnOnConfidence().getWeightedConfidenceValue()))
                .collect(Collectors.joining());
        final String message =
                String.format("Fresh air state changed to %s because of the confidence score %.2f\n%s", state, confidenceScore,
                        ruleValues);
        log.info(message);
    }
}
