package org.air_controller.web_access.card_view;

import org.air_controller.system_action.DurationCalculator;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemPersistenceData;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
class SystemCardViewService implements InterfaceService {

    private final SystemActionDbAccessor airFlowDbAccessor;

    public SystemCardViewService(SystemActionDbAccessor airFlowDbAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
    }

    @Override
    public CardView getCardView() {
        final Optional<SystemAction> systemAction = airFlowDbAccessor.getMostCurrentSystemAction();
        final String systemState = systemAction.map(SystemAction::outputState).map(outputState -> outputState.isOn() ? "ON" : "OFF").orElse("UNKNOWN");
        final CardItem systemStateItem = new CardItem("System State", systemState, "");
        return new CardView("", List.of(systemStateItem));
    }

    public CardView getConfidenceCardView() {
        final Optional<VentilationSystemPersistenceData> persistenceDataOptional = airFlowDbAccessor.getMostCurrentPersistenceData();
        if (persistenceDataOptional.isEmpty()) {
            return new CardView("No confidence data available", emptyList());
        }
        final VentilationSystemPersistenceData persistenceData = persistenceDataOptional.get();
        final String totalConfidence = doubleToString(persistenceData.totalConfidence());
        final List<CardItem> confidenceCards = mapToCardView(persistenceData.confidences());
        return new CardView("Total confidence of " + totalConfidence, confidenceCards);
    }

    public CardView getStatisticsCardView() {
        final double onPercentage = getOnPercentageFromLast24Hours();
        final CardItem cardItem = new CardItem("On during last 24h", doubleToString(onPercentage), "%");
        return new CardView("", List.of(cardItem));
    }

    private List<CardItem> mapToCardView(Map<String, Double> confidences) {
        return confidences.entrySet().stream()
                .map(e -> new CardItem(e.getKey(), doubleToString(e.getValue()), ""))
                .toList();
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
