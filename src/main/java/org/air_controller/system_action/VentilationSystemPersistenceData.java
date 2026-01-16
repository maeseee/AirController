package org.air_controller.system_action;

import org.air_controller.rules.Confidence;
import org.air_controller.system.OutputState;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

public record VentilationSystemPersistenceData(OutputState action, double totalConfidence, Map<String, Confidence> confidences, ZonedDateTime timestamp) {
    public static VentilationSystemPersistenceData create(OutputState action, double totalConfidence, Map<String, Confidence> confidences) {
        return new VentilationSystemPersistenceData(action, totalConfidence, confidences,  ZonedDateTime.now(ZoneOffset.UTC));
    }
}
