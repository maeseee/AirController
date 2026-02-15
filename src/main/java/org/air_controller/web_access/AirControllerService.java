package org.air_controller.web_access;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.MeasuredValue;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.web_access.graph.GraphItem;
import org.air_controller.web_access.graph.GraphView;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
public class AirControllerService {
    private static final int MAX_NUMBER_OF_ITEMS = 150;

    private final SystemActionDbAccessor airFlowDbAccessor;
    private final ClimateDataPointsDbAccessor indoorDataPointsAccessor;

    public AirControllerService(SystemActionDbAccessor airFlowDbAccessor, ClimateDataPointsDbAccessor indoorDataPointsAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
        this.indoorDataPointsAccessor = indoorDataPointsAccessor;
    }

    public Optional<SystemAction> getCurrentStateForFreshAir() {
        return airFlowDbAccessor.getMostCurrentSystemAction();
    }

    public GraphView getIndoorGraphOfMeasuredValues(MeasuredValue measuredValue, Duration duration) {
        final GraphView indoorGraph = createIndoorGraph(duration, measuredValue.getNameWithUnit(), measuredValue.getValueExtractor());
        log.info("Asking for indoor graph items of {} for a duration of {}. Returning a total of {} items", measuredValue.name(), duration,
                indoorGraph.items().size());
        return indoorGraph;
    }

    private GraphView createIndoorGraph(Duration duration, String title, Function<ClimateDataPoint, Double> valueExtractor) {
        final List<ClimateDataPoint> dataPoints = indoorDataPointsAccessor.getDataPoints(duration);
        final List<GraphItem> items = dataPoints.stream()
                .filter(dataPoint -> valueExtractor.apply(dataPoint) != null)
                .map(dataPoint -> new GraphItem(
                        toLocalDateTime(dataPoint.timestamp()),
                        valueExtractor.apply(dataPoint)
                ))
                .toList();
        return new GraphView(title, ItemReducer.reduceTo(items, MAX_NUMBER_OF_ITEMS));
    }

    private LocalDateTime toLocalDateTime(ZonedDateTime timestamp) {
        return timestamp
                .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime();
    }
}
