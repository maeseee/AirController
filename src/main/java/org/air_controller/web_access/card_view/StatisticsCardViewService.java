package org.air_controller.web_access.card_view;

import org.air_controller.system_action.DurationCalculator;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
class StatisticsCardViewService implements InterfaceService {

    private final SystemActionDbAccessor airFlowDbAccessor;

    public StatisticsCardViewService(SystemActionDbAccessor airFlowDbAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
    }

    @Override
    public CardView getCardView() {
        final double onPercentage = getOnPercentageFromLast24Hours();
        final CardItem cardItem = new CardItem("On during last 24h", doubleToString(onPercentage), "%");
        return new CardView("", List.of(cardItem));
    }

    private String doubleToString(Double value) {
        return String.format("%.2f", value);
    }

    private double getOnPercentageFromLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> actionsFromTimeToNow = airFlowDbAccessor.getActions(Duration.ofHours(26)); // just enough data
        final DurationCalculator durationCalculator = new DurationCalculator(actionsFromTimeToNow);
        final Duration duration = durationCalculator.getDuration(now.minusDays(1), now);
        return (double) duration.toSeconds() / (double) Duration.ofDays(1).toSeconds() * 100.0;
    }
}
