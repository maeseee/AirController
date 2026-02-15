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

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@Slf4j
public class AirControllerService {

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
        log.info("Asking for indoor graph items of {} for a duration of {}. Returning a total of {} items", measuredValue.name(), duration, indoorGraph.items().size());
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
        return new GraphView(title, reduceNumberOfGraphItems(items));
    }

    private LocalDateTime toLocalDateTime(ZonedDateTime timestamp) {
        return timestamp
                .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime();
    }

    private List<GraphItem> reduceNumberOfGraphItems(List<GraphItem> graphItems) {
        final int MAX_NUMBER_OF_ITEMS = 200;
        final int consistLastItem = graphItems.size() % 2;
        List<GraphItem> keptItems = graphItems;
        while (keptItems.size() > MAX_NUMBER_OF_ITEMS) {
            keptItems = IntStream.range(0, graphItems.size())
                    .filter(i -> (i + consistLastItem) % 2 == 0)
                    .mapToObj(graphItems::get)
                    .toList();
        }
        return keptItems;
    }
}
