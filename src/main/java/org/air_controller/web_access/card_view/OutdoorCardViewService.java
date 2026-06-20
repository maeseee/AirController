package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.springframework.stereotype.Service;

@Service
class OutdoorCardViewService implements CardViewService {

    private final ClimateDataPointsDbAccessor outdoorDataPointsAccessor;

    public OutdoorCardViewService(ClimateDataPointsDbAccessor outdoorDataPointsAccessor) {
        this.outdoorDataPointsAccessor = outdoorDataPointsAccessor;
    }

    @Override
    public CardView getCardView() {
        final ClimateDataPointsToCardView transformer = new ClimateDataPointsToCardView();
        return transformer.toCardView(outdoorDataPointsAccessor);
    }
}
