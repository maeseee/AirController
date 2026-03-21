package org.air_controller.web_access.graph;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.MeasuredValue;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
public class SensorGraphService {
    private final ClimateDataPointPersistence dbAccessor;

    public SensorGraphService(ClimateDataPointPersistence dbAccessor) {
        this.dbAccessor = dbAccessor;
    }

    public GraphView getGraphView(MeasuredValue measuredValue, Duration duration, String name) {
        final List<ClimateDataPoint> dataPoints = dbAccessor.getDataPoints(duration);
        final GraphView indoorGraph = createGraphView(dataPoints, measuredValue);
        log.info("Asking for {} graph items of type {} for a duration of {}. Returning a total of {} items", name, measuredValue.name(), duration,
                indoorGraph.items().size());
        return indoorGraph;
    }

    private GraphView createGraphView(List<ClimateDataPoint> dataPoints, MeasuredValue measuredValue) {
        final List<GraphItem> items = dataPoints.stream()
                .filter(dataPoint -> dataPoint.getValue(measuredValue) != null)
                .map(dataPoint -> new GraphItem(
                        TimeUtils.toLocalDateTime(dataPoint.timestamp()),
                        dataPoint.getValue(measuredValue),
                        "#4bc0c0"))
                .toList();
        final DynamicItemReducer itemReducer = new DynamicItemReducer(DynamicItemReducer.MAX_NUMBER_OF_ITEMS);
        return new GraphView(measuredValue.nameWithUnit(), itemReducer.reduce(items));
    }
}
