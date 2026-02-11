package org.air_controller.web_access;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.web_access.graph.GraphItem;
import org.air_controller.web_access.graph.GraphView;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
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

    public GraphView getIndoorTemperatureGraph() {
        return createGraph("Temperature (°C)", dataPoint -> dataPoint.temperature().celsius());
    }

    public GraphView getIndoorHumidityGraph() {
        return createGraph("Humidity (%)", dataPoint -> dataPoint.humidity().getRelativeHumidity(dataPoint.temperature()));
    }

    public GraphView getIndoorCarbonDioxidGraph() {
        return createGraph("CO2 (ppm)", dataPoint -> dataPoint.co2().map(CarbonDioxide::ppm).orElse(null));
    }

    private GraphView createGraph(String title, Function<ClimateDataPoint, Double> valueExtractor) {
        final List<ClimateDataPoint> dataPoints = indoorDataPointsAccessor.getDataPointsFromLast24Hours();
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
        while (graphItems.size() > MAX_NUMBER_OF_ITEMS) {
            keptItems = IntStream.range(0, graphItems.size())
                    .filter(i -> (i + consistLastItem) % 2 == 0)
                    .mapToObj(graphItems::get)
                    .toList();
        }
        return keptItems;
    }
}
