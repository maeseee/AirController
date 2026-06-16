package org.air_controller.sensor.open_weather_api_adapter;

import org.air_controller.web_access.card_view.CardItem;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record SolarEvent(ZonedDateTime sunrise, ZonedDateTime sunset) {

    public CardItem sunriseToCardItem() {
        return new CardItem("Sunrise", eventTime(sunrise), "");
    }

    public CardItem sunsetToCardItem() {
        return new CardItem("Sunset", eventTime(sunset), "");
    }

    private String eventTime(ZonedDateTime event) {
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        final LocalDateTime eventAtTimezone = event.withZoneSameInstant(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime();
        return eventAtTimezone.format(timeFormatter);
    }
}
