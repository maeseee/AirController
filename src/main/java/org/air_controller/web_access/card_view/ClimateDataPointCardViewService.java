package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;

class ClimateDataPointCardViewService implements CardViewService {

    private final ClimateDataPointsDbAccessor climateDataPointAccessor;

    public ClimateDataPointCardViewService(ClimateDataPointsDbAccessor climateDataPointAccessor) {
        this.climateDataPointAccessor = climateDataPointAccessor;
    }

    @Override
    public CardView getCardView() {
        final ClimateDataPointsToCardView transformer = new ClimateDataPointsToCardView();
        return transformer.toCardView(climateDataPointAccessor);
    }
}
