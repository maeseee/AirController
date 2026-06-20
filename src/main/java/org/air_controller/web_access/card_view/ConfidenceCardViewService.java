package org.air_controller.web_access.card_view;

import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemPersistenceData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
class ConfidenceCardViewService implements CardViewService {

    private final SystemActionDbAccessor airFlowDbAccessor;

    public ConfidenceCardViewService(SystemActionDbAccessor airFlowDbAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
    }

    @Override
    public CardView getCardView() {
        final Optional<VentilationSystemPersistenceData> persistenceDataOptional = airFlowDbAccessor.getMostCurrentPersistenceData();
        if (persistenceDataOptional.isEmpty()) {
            return new CardView("No confidence data available", emptyList());
        }
        final VentilationSystemPersistenceData persistenceData = persistenceDataOptional.get();
        final String totalConfidence = doubleToString(persistenceData.totalConfidence());
        final List<CardItem> confidenceCards = mapToCardView(persistenceData.confidences());
        return new CardView("Total confidence of " + totalConfidence, confidenceCards);
    }

    private List<CardItem> mapToCardView(Map<String, Double> confidences) {
        return confidences.entrySet().stream()
                .map(e -> new CardItem(e.getKey(), doubleToString(e.getValue()), ""))
                .toList();
    }

    private String doubleToString(Double value) {
        return String.format("%.2f", value);
    }
}
