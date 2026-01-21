package org.air_controller.system_action;

import org.air_controller.rules.Confidence;
import org.air_controller.system.OutputState;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

public record VentilationSystemPersistenceData(OutputState action, double totalConfidence, Map<String, Confidence> confidences, ZonedDateTime timestamp) {
    public static VentilationSystemPersistenceData create(OutputState action, double totalConfidence, Map<String, Confidence> confidences) {
        return new VentilationSystemPersistenceData(action, totalConfidence, confidences,  ZonedDateTime.now(ZoneOffset.UTC));
    }

    public String getConfidencesText() {
        return confidences.entrySet().stream()
                .map(confidence -> confidence.getKey() + ": " + confidence.getValue().getWeightedConfidenceValueString())
                .collect(Collectors.joining(",  "));
    }

    public SystemAction getAction() {
        return new SystemAction(timestamp, action);
    }

    public static Map<String, Confidence> toConfidencesMap(String confidencesString) {
        if (confidencesString == null || confidencesString.trim().isEmpty()) {
            return emptyMap();
        }

        return Arrays.stream(confidencesString.split(",\\s{2}"))
                .map(String::trim)
                .filter(pair -> !pair.isEmpty())
                .map(pair -> pair.split(":\\s", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> new Confidence(Double.parseDouble(parts[1]), 1.0)
                ));
    }
}
