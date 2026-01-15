package org.air_controller.system_action;

import org.air_controller.rules.Confidence;
import org.air_controller.system.OutputState;

import java.util.Map;

public record VentilationSystemPersistenceData(OutputState action, double totalConfidence, Map<String, Confidence> confidences) {
}
