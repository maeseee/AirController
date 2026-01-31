package org.air_controller.web_access;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.CurrentClimateDataPoint;
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
public class AirControllerService {

    private static final Duration INFO_DURATION = Duration.ofMinutes(10);

    private final SystemActionDbAccessor airFlowDbAccessor;
    private final ClimateDataPointsDbAccessor indoorDataPointsAccessor;
    private final ClimateDataPointsDbAccessor outdoorDataPointsAccessor;

    public AirControllerService(SystemActionDbAccessor airFlowDbAccessor, ClimateDataPointsDbAccessor indoorDataPointsAccessor,
            ClimateDataPointsDbAccessor outdoorDataPointsAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
        this.indoorDataPointsAccessor = indoorDataPointsAccessor;
        this.outdoorDataPointsAccessor = outdoorDataPointsAccessor;
    }

    public Optional<SystemAction> getCurrentStateForFreshAir() {
        return airFlowDbAccessor.getMostCurrentSystemAction();
    }

    public CardGroup getIndoorCardGroup() {
        return getCardGroup(indoorDataPointsAccessor);
    }

    public CardGroup getOutdoorCardGroup() {
        return getCardGroup(outdoorDataPointsAccessor);
    }

    public Optional<Double> getCurrentTotalConfidence() {
        return airFlowDbAccessor.getMostCurrentPersistenceData().map(VentilationSystemPersistenceData::totalConfidence);
    }

    public Optional<Map<String, Double>> getCurrentConfidences() {
        return airFlowDbAccessor.getMostCurrentPersistenceData().map(VentilationSystemPersistenceData::confidences);
    }

    public double getOnPercentageFromTheLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = now.minusDays(1);
        final List<SystemAction> actionsFromTimeToNow = airFlowDbAccessor.getActionsFromTimeToNow(startTime.minusHours(2)); // just enough data
        final DurationCalculator durationCalculator = new DurationCalculator(actionsFromTimeToNow);
        final Duration duration = durationCalculator.getDuration(startTime, now);
        return (double) duration.toMinutes() / (double) Duration.ofDays(1).toMinutes();
    }

    private CardGroup getCardGroup(ClimateDataPointsDbAccessor dataPointsAccessor) {
        final CurrentClimateDataPoint currentClimateDataPoint = new CurrentClimateDataPoint(dataPointsAccessor);
        final Optional<ClimateDataPoint> dataPointOptional = currentClimateDataPoint.getCurrentClimateDataPoint();
        return mapToCardGroup(dataPointOptional);
    }

    private CardGroup mapToCardGroup(Optional<ClimateDataPoint> dataPointOptional) {
        if (dataPointOptional.isEmpty()) {
            return new CardGroup("No cards available", emptyList());
        }
        final List<CardView> cardViews = dataPointOptional.get().getCardViews();
        final ZonedDateTime timestamp = dataPointOptional.get().timestamp();
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final Duration cardAge = Duration.between(timestamp, now);
        final String info = cardAge.compareTo(INFO_DURATION) > 0 ? "Last sensor update was " + cardAge.toMinutes() + " minutes ago" : "";
        return new CardGroup(info, cardViews);
    }
}
