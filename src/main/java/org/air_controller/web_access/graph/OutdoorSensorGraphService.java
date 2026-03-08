package org.air_controller.web_access.graph;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.MeasuredValue;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
class OutdoorSensorGraphService {
    private static final int MAX_NUMBER_OF_ITEMS = 150;

    private final ClimateDataPointsDbAccessor outdoorDataPointsAccessor;

    public OutdoorSensorGraphService(ClimateDataPointsDbAccessor outdoorDataPointsAccessor) {
        this.outdoorDataPointsAccessor = outdoorDataPointsAccessor;
    }

    public GraphView getOutdoorGraphOfMeasuredValues(MeasuredValue measuredValue, Duration duration) {
        final List<ClimateDataPoint> dataPoints = outdoorDataPointsAccessor.getDataPoints(duration);
        final GraphView outdoorGraph = createGraphView(dataPoints, measuredValue);
        log.info("Asking for outdoor graph items of {} for a duration of {}. Returning a total of {} items", measuredValue.name(), duration,
                outdoorGraph.items().size());
        return outdoorGraph;
    }

    private GraphView createGraphView(List<ClimateDataPoint> dataPoints, MeasuredValue measuredValue) {
        final List<GraphItem> items = dataPoints.stream()
                .filter(dataPoint -> dataPoint.getValue(measuredValue) != null)
                .map(dataPoint -> new GraphItem(
                        TimeUtils.toLocalDateTime(dataPoint.timestamp()),
                        dataPoint.getValue(measuredValue)
                ))
                .toList();
        return new GraphView(measuredValue.nameWithUnit(), ItemReducer.reduceTo(items, MAX_NUMBER_OF_ITEMS));
    }
}
