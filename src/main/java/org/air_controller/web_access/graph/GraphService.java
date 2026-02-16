package org.air_controller.web_access.graph;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.MeasuredValue;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.web_access.ItemReducer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class GraphService {
    private static final int MAX_NUMBER_OF_ITEMS = 150;

    private final SystemActionDbAccessor airFlowDbAccessor;
    private final ClimateDataPointsDbAccessor outdoorDataPointsAccessor;

    public GraphService(SystemActionDbAccessor airFlowDbAccessor, ClimateDataPointsDbAccessor outdoorDataPointsAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
        this.outdoorDataPointsAccessor = outdoorDataPointsAccessor;
    }

    public GraphView getAirflowGraphValues(Duration duration) {
        final List<SystemAction> dataPoints = airFlowDbAccessor.getActions(duration);
        final GraphView indoorGraph = createGraphView(dataPoints);
        log.info("Asking for system fresh air status graph for a duration of {}. Returning a total of {} items", duration,
                indoorGraph.items().size());
        return indoorGraph;
    }

    public GraphView getOutdoorGraphOfMeasuredValues(MeasuredValue measuredValue, Duration duration) {
        final List<ClimateDataPoint> dataPoints = outdoorDataPointsAccessor.getDataPoints(duration);
        final GraphView outdoorGraph = createGraphView(dataPoints, measuredValue.getNameWithUnit(), measuredValue.getValueExtractor());
        log.info("Asking for outdoor graph items of {} for a duration of {}. Returning a total of {} items", measuredValue.name(), duration,
                outdoorGraph.items().size());
        return outdoorGraph;
    }

    private GraphView createGraphView(List<SystemAction> dataPoints) {
        final List<GraphItem> items = dataPoints.stream()
                .map(dataPoint -> new GraphItem(
                        toLocalDateTime(dataPoint.actionTime()),
                        dataPoint.outputState().isOn() ? 1.0 : 0.0))
                .toList();
        return new GraphView("Air flow ON/OFF", ItemReducer.reduceTo(items, MAX_NUMBER_OF_ITEMS));
    }

    private GraphView createGraphView(List<ClimateDataPoint> dataPoints, String title, Function<ClimateDataPoint, Double> valueExtractor) {
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
