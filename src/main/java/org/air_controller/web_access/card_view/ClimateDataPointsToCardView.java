package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.CurrentClimateDataPoint;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

class ClimateDataPointsToCardView {

    private static final Duration INFO_DURATION = Duration.ofMinutes(10);

    CardView toCardView(ClimateDataPointsDbAccessor dataPointsAccessor) {
        final CurrentClimateDataPoint currentClimateDataPoint = new CurrentClimateDataPoint(dataPointsAccessor);
        final Optional<ClimateDataPoint> dataPointOptional = currentClimateDataPoint.getCurrentClimateDataPoint();
        return mapToCardView(dataPointOptional);
    }

    private CardView mapToCardView(Optional<ClimateDataPoint> dataPointOptional) {
        if (dataPointOptional.isEmpty()) {
            return new CardView("No cards available", emptyList());
        }
        final List<CardItem> cardItems = dataPointOptional.get().getCardItems();
        final ZonedDateTime timestamp = dataPointOptional.get().timestamp();
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final Duration cardAge = Duration.between(timestamp, now);
        final String info = cardAge.compareTo(INFO_DURATION) > 0 ? "Last sensor update was " + cardAge.toMinutes() + " minutes ago" : "";
        return new CardView(info, cardItems);
    }
}
