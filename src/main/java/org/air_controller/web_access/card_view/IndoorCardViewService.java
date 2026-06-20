package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.springframework.stereotype.Service;

@Service
class IndoorCardViewService implements InterfaceService{

    private final ClimateDataPointsDbAccessor indoorDataPointsAccessor;

    public IndoorCardViewService(ClimateDataPointsDbAccessor indoorDataPointsAccessor) {
        this.indoorDataPointsAccessor = indoorDataPointsAccessor;
    }

    @Override
    public CardView getCardView() {
        final ClimateDataPointsToCardView transformer = new ClimateDataPointsToCardView();
        return transformer.toCardView(indoorDataPointsAccessor);
    }
}
