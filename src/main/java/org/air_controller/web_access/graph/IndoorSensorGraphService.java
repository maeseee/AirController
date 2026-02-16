package org.air_controller.web_access.graph;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.MeasuredValue;
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
public class IndoorSensorGraphService {
    private static final int MAX_NUMBER_OF_ITEMS = 150;

    private final ClimateDataPointsDbAccessor indoorDataPointsAccessor;

    public IndoorSensorGraphService(ClimateDataPointsDbAccessor indoorDataPointsAccessor) {
        this.indoorDataPointsAccessor = indoorDataPointsAccessor;
    }

    public GraphView getIndoorGraphOfMeasuredValues(MeasuredValue measuredValue, Duration duration) {
        final List<ClimateDataPoint> dataPoints = indoorDataPointsAccessor.getDataPoints(duration);
        final GraphView indoorGraph = createGraphView(dataPoints, measuredValue.getNameWithUnit(), measuredValue.getValueExtractor());
        log.info("Asking for indoor graph items of {} for a duration of {}. Returning a total of {} items", measuredValue.name(), duration,
                indoorGraph.items().size());
        return indoorGraph;
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
