package org.air_controller;

import lombok.RequiredArgsConstructor;
import org.air_controller.rules.Rule;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SystemStateLogger implements Runnable {
    private static final Logger logger = LogManager.getLogger(SystemStateLogger.class);

    private final VentilationSystem ventilationSystem;
    private final List<Rule> freshAirRules;

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            logger.error("Exception in AirController loop:", exception);
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
        logger.info(message);
    }
}
