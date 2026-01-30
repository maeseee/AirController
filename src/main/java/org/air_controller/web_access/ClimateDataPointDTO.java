package org.air_controller.web_access;

@Deprecated
public record ClimateDataPointDTO(double temperatureInCelsius, double relativeHumidity, Double co2InPpm) {
}
