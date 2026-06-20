package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.CurrentClimateDataPoint;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
class OutdoorCardViewService implements InterfaceService {

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
