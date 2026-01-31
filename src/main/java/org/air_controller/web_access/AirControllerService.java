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

    @Deprecated
    public Optional<ClimateDataPointDTO> getCurrentIndoorClimateDataPoint() {
        return getCurrentClimateDataPoint(indoorDataPointsAccessor);
    }

    public CardGroup getIndoorCardGroup() {
        return getCardGroup(indoorDataPointsAccessor);
    }

    @Deprecated
    public Optional<ClimateDataPointDTO> getCurrentOutdoorClimateDataPoint() {
        return getCurrentClimateDataPoint(outdoorDataPointsAccessor);
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

    @Deprecated
    private Optional<ClimateDataPointDTO> getCurrentClimateDataPoint(ClimateDataPointsDbAccessor dataPointsAccessor) {
        final CurrentClimateDataPoint currentClimateDataPoint = new CurrentClimateDataPoint(dataPointsAccessor);
        final Optional<ClimateDataPoint> dataPointOptional = currentClimateDataPoint.getCurrentClimateDataPoint();
        return mapToClimateDataPointDTO(dataPointOptional);
    }

    private CardGroup getCardGroup(ClimateDataPointsDbAccessor dataPointsAccessor) {
        final CurrentClimateDataPoint currentClimateDataPoint = new CurrentClimateDataPoint(dataPointsAccessor);
        final Optional<ClimateDataPoint> dataPointOptional = currentClimateDataPoint.getCurrentClimateDataPoint();
        return mapToCardGroup(dataPointOptional);
    }

    @Deprecated
    private Optional<ClimateDataPointDTO> mapToClimateDataPointDTO(Optional<ClimateDataPoint> dataPointOptional) {
        return dataPointOptional.map(AirControllerService::toClimateDataPointDTO);
    }

    private CardGroup mapToCardGroup(Optional<ClimateDataPoint> dataPointOptional) {
        if (dataPointOptional.isEmpty()) {
            return new CardGroup(ZonedDateTime.now(ZoneOffset.UTC), emptyList());
        }
        final List<CardView> cardViews = dataPointOptional.get().getCardViews();
        return new CardGroup(dataPointOptional.get().timestamp(), cardViews);
    }

    @Deprecated
    private static ClimateDataPointDTO toClimateDataPointDTO(ClimateDataPoint dataPoint) {
        final double celsius = dataPoint.temperature().celsius();
        final double relativeHumidity = dataPoint.humidity().getRelativeHumidity(dataPoint.temperature());
        final Double co2 = dataPoint.co2().map(CarbonDioxide::ppm).orElse(null);
        return new ClimateDataPointDTO(celsius, relativeHumidity, co2);
    }
}
