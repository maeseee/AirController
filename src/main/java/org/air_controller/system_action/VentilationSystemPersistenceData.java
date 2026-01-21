package org.air_controller.system_action;

import org.air_controller.system.OutputState;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record VentilationSystemPersistenceData(OutputState action, double totalConfidence, Map<String, Double> confidences, ZonedDateTime timestamp) {

    public static VentilationSystemPersistenceData create(OutputState action, double totalConfidence, Map<String, Double> confidences) {
        return new VentilationSystemPersistenceData(action, totalConfidence, confidences,  ZonedDateTime.now(ZoneOffset.UTC));
    }

    public static VentilationSystemPersistenceData create(OutputState action, double totalConfidence, String confidencesString, ZonedDateTime timestamp) {
        if (confidencesString == null || confidencesString.trim().isEmpty()) {
            throw new IllegalArgumentException("confidencesString cannot be null or empty");
        }

        final Map<String, Double> confidences = Arrays.stream(confidencesString.split(",\\s{2}"))
                .map(String::trim)
                .filter(pair -> !pair.isEmpty())
                .map(pair -> pair.split(":\\s", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> Double.parseDouble(parts[1])
                ));
        return new VentilationSystemPersistenceData(action, totalConfidence, confidences,  timestamp);
    }

    public String getConfidencesText() {
        return confidences.entrySet().stream()
                .map(confidence -> confidence.getKey() + ": " + confidence.getValue())
                .collect(Collectors.joining(",  "));
    }

    public SystemAction getAction() {
        return new SystemAction(timestamp, action);
    }
}
