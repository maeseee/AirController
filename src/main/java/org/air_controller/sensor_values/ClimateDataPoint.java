package org.air_controller.sensor_values;

import org.air_controller.web_access.card.CardView;
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

    public List<CardView> getCardViews() {
        final ArrayList<CardView> cardViews = new ArrayList<>();
        final CardView temperatureView = temperature.toCardView();
        cardViews.add(temperatureView);
        final CardView humidityView = humidity.toCardView(temperature);
        cardViews.add(humidityView);
        if (co2.isPresent()) {
            final CardView co2View = co2.get().toCardView();
            cardViews.add(co2View);
        }
        return cardViews;
    }
}
