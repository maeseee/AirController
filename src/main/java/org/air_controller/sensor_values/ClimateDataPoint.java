package org.air_controller.sensor_values;

import org.air_controller.web_access.card.CardItem;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ClimateDataPoint(Temperature temperature, Humidity humidity, Optional<CarbonDioxide> co2, ZonedDateTime timestamp) {

    @Override
    public @NotNull String toString() {
        return "ClimateDataPoint{" +
                temperature.toCardItem() +
                ", " + humidity.toCardItem(temperature) +
                co2.map(carbonDioxide -> ", " + carbonDioxide.toCardItem()).orElse("") +
                "}";
    }

    public List<CardItem> getCardItems() {
        final ArrayList<CardItem> cardItems = new ArrayList<>();
        final CardItem temperatureView = temperature.toCardItem();
        cardItems.add(temperatureView);
        final CardItem humidityView = humidity.toCardItem(temperature);
        cardItems.add(humidityView);
        if (co2.isPresent()) {
            final CardItem co2View = co2.get().toCardItem();
            cardItems.add(co2View);
        }
        return cardItems;
    }
}
