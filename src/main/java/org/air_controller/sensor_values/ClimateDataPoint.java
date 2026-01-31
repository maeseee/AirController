package org.air_controller.sensor_values;

import org.air_controller.web_access.CardView;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ClimateDataPoint(Temperature temperature, Humidity humidity, Optional<CarbonDioxide> co2, ZonedDateTime timestamp) {

    @Override
    public @NotNull String toString() {
        return "ClimateDataPoint{" +
                temperature.toCardView() +
                ", " + humidity.toCardView(temperature) +
                co2.map(carbonDioxide -> ", " + carbonDioxide.toCardView()).orElse("") +
                "}";
    }
}
