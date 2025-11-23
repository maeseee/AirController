package org.air_controller.sensor_values;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Optional;

public record ClimateDataPoint(Temperature temperature, Humidity humidity, Optional<CarbonDioxide> co2, ZonedDateTime timestamp) {

    @Override
    public @NotNull String toString() {
        return "ClimateDataPoint{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                co2.map(carbonDioxide -> ", co2=" + carbonDioxide).orElse("") +
                '}';
    }
}
