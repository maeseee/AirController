package org.air_controller.web_access;

public record ClimateDataPointDTO(double temperatureInCelsius, double relativeHumidity, Double co2InPpm) {
}
