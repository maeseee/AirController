package org.air_controller.web_access;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.web_access.graph.GraphItem;
import org.air_controller.web_access.graph.GraphView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        final List<ClimateDataPoint> dataPoints = indoorDataPointsAccessor.getDataPointsFromLast24Hours();
        final List<GraphItem> temperatureItems = dataPoints.stream()
                .map(dataPoint -> new GraphItem(
                        dataPoint.timestamp(),
                        dataPoint.temperature().celsius()
                ))
                .toList();
        return new GraphView("Temperature (°C)", reduceNumberOfGraphItems(temperatureItems));
    }

    public GraphView getIndoorHumidityGraph() {
        final List<ClimateDataPoint> dataPoints = indoorDataPointsAccessor.getDataPointsFromLast24Hours();
        final List<GraphItem> humidityItems = dataPoints.stream()
                .map(dataPoint -> new GraphItem(
                        dataPoint.timestamp(),
                        dataPoint.humidity().getRelativeHumidity(dataPoint.temperature())
                ))
                .toList();
        return new GraphView("Humidity (%)", reduceNumberOfGraphItems(humidityItems));
    }

    public GraphView getIndoorCarbonDioxidGraph() {
        final List<ClimateDataPoint> dataPoints = indoorDataPointsAccessor.getDataPointsFromLast24Hours();
        final List<GraphItem> humidityItems = dataPoints.stream()
                .filter(dataPoint -> dataPoint.co2().isPresent())
                .map(dataPoint -> new GraphItem(
                        dataPoint.timestamp(),
                        dataPoint.co2()
                                .map(CarbonDioxide::ppm)
                                .orElseThrow()
                ))
                .toList();
        return new GraphView("CO2 (ppm)", reduceNumberOfGraphItems(humidityItems));
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
